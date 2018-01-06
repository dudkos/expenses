package com.finanse.search.api.service;

import com.finance.common.context.UserContext;
import com.finance.common.dto.SearchTransactions;
import com.finance.common.dto.Transaction;
import com.finance.common.exception.ServiceException;
import com.finanse.search.api.util.SearchApiConstants;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

@Service
public class TransactionsSearchServiceImpl implements TransactionsSearchService, SearchApiConstants {

    private static final Logger logger = LoggerFactory.getLogger(TransactionsSearchServiceImpl.class);

    private final TransportClient transportClient;

    private final UserContext userContext;

    private final static SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

    @Value("${elasticsearch.response.size.default}")
    private Integer defaultSize;

    @Autowired
    public TransactionsSearchServiceImpl(TransportClient transportClient, UserContext userContext) {
        this.transportClient = transportClient;
        this.userContext = userContext;
    }

    @Override
    public void indexTransactions(SearchTransactions searchTransactions) {

        isInputDataValid(searchTransactions);

        BulkRequestBuilder requestBuilder = transportClient.prepareBulk();
        searchTransactions.getExpensesTransactions().forEach(v -> createSingleIndex(userContext.getUserId(), searchTransactions.getCategoryId(), v, requestBuilder));

        BulkResponse bulkResponse = requestBuilder.get();
        if (bulkResponse.hasFailures()) {
            throw new ServiceException(500, bulkResponse.buildFailureMessage());
        }

        logger.info("{} transactions for user with id {} have been added to the index",
                searchTransactions.getExpensesTransactions().size(), userContext.getUserId());
    }

    @Override
    public void updateTransactions(SearchTransactions searchTransactions) {

        isInputDataValid(searchTransactions);

        BulkRequestBuilder requestBuilder = transportClient.prepareBulk();
        searchTransactions.getExpensesTransactions().forEach(v -> updateSingleIndex(v, searchTransactions.getCategoryId(), requestBuilder));
        BulkResponse bulkResponse = requestBuilder.get();

        if (bulkResponse.hasFailures()) {
            throw new ServiceException(500, bulkResponse.buildFailureMessage());
        }

        logger.info("{} transactions for user with id {} have been updated in index",
                searchTransactions.getExpensesTransactions().size(), userContext.getUserId());
    }

    @Override
    public void deleteFromIndexByUserId(Integer userId) {

        isUserIdValid(userId);

        DeleteByQueryAction.INSTANCE.newRequestBuilder(transportClient)
                .filter(QueryBuilders.matchQuery(USER_ID, userId))
                .source(TRANSACTION_INDEX)
                .execute(new ActionListener<BulkByScrollResponse>() {

                    @Override
                    public void onResponse(BulkByScrollResponse bulkByScrollResponse) {
                        logger.info("{} transactions for user with id {} have been deleted from index", bulkByScrollResponse.getDeleted(), userId);
                    }

                    @Override
                    public void onFailure(Exception e) {
                        throw new ServiceException(500, "Error during deletion from index for user "+userId+" : " + e.getMessage());
                    }
                });
    }

    @Override
    public List<Transaction> searchTransactions(Integer categoryId, String desc, Integer size) {

        try {
            isInputParametersValid(userContext.getUserId(), categoryId);
        } catch (ServiceException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }

        SearchResponse response = transportClient.prepareSearch(TRANSACTION_INDEX)
                .setTypes(TRANSACTION_TYPE)
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery(USER_ID, userContext.getUserId()))
                        .must(QueryBuilders.termQuery(CATEGORY_ID, categoryId))
                        .must(QueryBuilders.fuzzyQuery(DESCRIPTION, desc)))
                .setSize(size == null ? defaultSize : size)
                .get();

        return convertToExpensesTransactions(response);
    }

    private void createSingleIndex(Integer userId, Integer categoryId, Transaction expensesTransaction, BulkRequestBuilder requestBuilder) {
        try {
            requestBuilder.add(transportClient.prepareIndex(TRANSACTION_INDEX, TRANSACTION_TYPE, expensesTransaction.getId().toString())
                    .setSource(jsonBuilder()
                            .startObject()
                            .field(USER_ID, userId)
                            .field(CATEGORY_ID, categoryId)
                            .field(DATE, sdf.format(expensesTransaction.getTransactionDate()))
                            .field(DESCRIPTION, expensesTransaction.getDescription())
                            .field(AMOUNT, expensesTransaction.getAmount().doubleValue())
                            .field(CODE, expensesTransaction.getCurrencyCode())
                            .endObject()
                    ));
        } catch (IOException e) {
            logger.error("error creating single index for user {} and category id {}", userId, categoryId);
        }
    }

    private List<Transaction> convertToExpensesTransactions(SearchResponse response) {
        List<Transaction> expensesTransactions = new ArrayList<>();
        if(response.getHits() != null || response.getHits().getTotalHits() != 0) {
            response.getHits().forEach(v -> {
                Transaction transaction = convertToExpensesTransaction(v.getId(), v.getSource());
                if(transaction != null) {
                    expensesTransactions.add(transaction);
                }
            });
        }

        return expensesTransactions;
    }

    private Transaction convertToExpensesTransaction(String transactionId, Map<String, Object> source) {
        if(transactionId != null) {
            return new Transaction() {
                @Override
                public Integer getId() {
                    return Integer.valueOf(transactionId);
                }

                @Override
                public String getCurrencyCode() {
                    return source.get(CODE).toString();
                }

                @Override
                public Timestamp getTransactionDate() {
                    try {
                        return new Timestamp(sdf.parse(source.get(DATE).toString()).getTime());
                    } catch (ParseException e) {
                        logger.error("can't parse date - " + e.getMessage());
                    }

                    return null;
                }

                @Override
                public BigDecimal getAmount() {
                    return BigDecimal.valueOf(Double.valueOf(source.get(AMOUNT).toString()));
                }

                @Override
                public String getDescription() {
                    return source.get(DESCRIPTION).toString();
                }
            };
        }

        return null;
    }

    private void updateSingleIndex(Transaction expensesTransaction, Integer categoryId, BulkRequestBuilder requestBuilder) {
        try {
            requestBuilder.add(transportClient.prepareUpdate(TRANSACTION_INDEX, TRANSACTION_TYPE, expensesTransaction.getId().toString())
                    .setDoc(jsonBuilder()
                            .startObject()
                            .field(CATEGORY_ID, categoryId)
                            .endObject()
                    ));
        } catch (IOException e) {
            logger.error("Error of building update index for category {} - {}", categoryId, e.getMessage());
        }

    }

    private void isInputParametersValid(Integer userId, Integer categoryId) {
        isUserIdValid(userId);
        isCategoryIdValid(categoryId);
    }

    private void isInputDataValid(SearchTransactions searchTransactions) {
        isInputSearchTransactionsValid(searchTransactions);
        isInputParametersValid(userContext.getUserId(), searchTransactions.getCategoryId());
    }

    private void isCategoryIdValid(Integer categoryId) {
        if(categoryId == null || categoryId == 0) {
            throw new ServiceException(400, "Category id is Invalid - "+categoryId);
        }
    }

    private void isInputSearchTransactionsValid(SearchTransactions searchTransactions) {
        if(searchTransactions == null) {
            throw new ServiceException(400, "Bad request. Request body is - null");
        } else if (searchTransactions.getExpensesTransactions().isEmpty()) {
            throw new ServiceException(400, "Bad request. Please check request body.");
        }
    }

    private void isUserIdValid(Integer userId) {
       if(userId == null || userId == 0) {
           throw new ServiceException(400, "User id is Invalid - " + userId);
       }
    }
}
