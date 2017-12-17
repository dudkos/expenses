package com.finanse.search.api.service;

import com.finanse.search.api.model.ExpensesTransaction;
import com.finanse.search.api.model.SearchTransactions;
import com.finanse.search.api.util.OAuth2Util;
import com.finanse.search.api.util.SearchApiConstants;
import com.finanse.search.api.util.SearchServiceException;
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

    private final OAuth2Util oAuth2Util;

    private final static SimpleDateFormat sdf = new SimpleDateFormat(DATE_PATTERN);

    @Value("${elasticsearch.response.size.default}")
    private Integer defaultSize;

    @Autowired
    public TransactionsSearchServiceImpl(TransportClient transportClient, OAuth2Util oAuth2Util) {
        this.transportClient = transportClient;
        this.oAuth2Util = oAuth2Util;
    }

    @Override
    public void indexTransactions(SearchTransactions searchTransactions) {

        isInputDataValid(searchTransactions);

        BulkRequestBuilder requestBuilder = transportClient.prepareBulk();
        searchTransactions.getExpensesTransactions().forEach(v -> createSingleIndex(oAuth2Util.getUserIdFromAuth(), searchTransactions.getCategoryId(), v, requestBuilder));

        BulkResponse bulkResponse = requestBuilder.get();
        if (bulkResponse.hasFailures()) {
            throw new SearchServiceException(500, bulkResponse.buildFailureMessage());
        }

        logger.info("{} transactions for user with id {} have been added to the index",
                searchTransactions.getExpensesTransactions().size(), oAuth2Util.getUserIdFromAuth());
    }

    @Override
    public void updateTransactions(SearchTransactions searchTransactions) {

        isInputDataValid(searchTransactions);

        BulkRequestBuilder requestBuilder = transportClient.prepareBulk();
        searchTransactions.getExpensesTransactions().forEach(v -> updateSingleIndex(v, searchTransactions.getCategoryId(), requestBuilder));
        BulkResponse bulkResponse = requestBuilder.get();

        if (bulkResponse.hasFailures()) {
            throw new SearchServiceException(500, bulkResponse.buildFailureMessage());
        }

        logger.info("{} transactions for user with id {} have been updated in index",
                searchTransactions.getExpensesTransactions().size(), oAuth2Util.getUserIdFromAuth());
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
                        throw new SearchServiceException(500, "Error during deletion from index for user "+userId+" : " + e.getMessage());
                    }
                });
    }

    @Override
    public List<ExpensesTransaction> searchTransactions(Integer categoryId, String desc, Integer size) {

        try {
            isInputParametersValid(oAuth2Util.getUserIdFromAuth(), categoryId);
        } catch (SearchServiceException e) {
            logger.error(e.getMessage());
            return new ArrayList<>();
        }

        SearchResponse response = transportClient.prepareSearch(TRANSACTION_INDEX)
                .setTypes(TRANSACTION_TYPE)
                .setQuery(QueryBuilders.boolQuery()
                        .must(QueryBuilders.termQuery(USER_ID, oAuth2Util.getUserIdFromAuth()))
                        .must(QueryBuilders.termQuery(CATEGORY_ID, categoryId))
                        .must(QueryBuilders.fuzzyQuery(DESCRIPTION, desc)))
                .setSize(size == null ? defaultSize : size)
                .get();

        return convertToExpensesTransactions(response);
    }

    @Override
    public List<ExpensesTransaction> searchTransactions(Integer categoryId, List<Long> ids) {
        throw new UnsupportedOperationException();
    }

    private void createSingleIndex(Integer userId, Integer categoryId, ExpensesTransaction expensesTransaction, BulkRequestBuilder requestBuilder) {
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

    private List<ExpensesTransaction> convertToExpensesTransactions(SearchResponse response) {
        List<ExpensesTransaction> expensesTransactions = new ArrayList<>();
        if(response.getHits() != null || response.getHits().getTotalHits() != 0) {
            response.getHits().forEach(v -> {
                ExpensesTransaction transaction = convertToExpensesTransaction(v.getId(), v.getSource());
                if(transaction != null) {
                    expensesTransactions.add(transaction);
                }
            });
        }

        return expensesTransactions;
    }

    private ExpensesTransaction convertToExpensesTransaction(String transactionId, Map<String, Object> source) {
        if(transactionId != null) {
            ExpensesTransaction expensesTransaction = new ExpensesTransaction();
            expensesTransaction.setId(Integer.valueOf(transactionId));
            try {
                expensesTransaction.setTransactionDate(new Timestamp(sdf.parse(source.get(DATE).toString()).getTime()));
            } catch (ParseException e) {
                logger.error("can't parse date - " + e.getMessage());
            }
            expensesTransaction.setDescription(source.get(DESCRIPTION).toString());
            expensesTransaction.setAmount(BigDecimal.valueOf(Double.valueOf(source.get(AMOUNT).toString())));
            expensesTransaction.setCurrencyCode(source.get(CODE).toString());

            return expensesTransaction;
        }

        return null;
    }

    private void updateSingleIndex(ExpensesTransaction expensesTransaction, Integer categoryId, BulkRequestBuilder requestBuilder) {
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
        isInputParametersValid(oAuth2Util.getUserIdFromAuth(), searchTransactions.getCategoryId());
    }

    private void isCategoryIdValid(Integer categoryId) {
        if(categoryId == null || categoryId == 0) {
            throw new SearchServiceException(400, "Category id is Invalid - "+categoryId);
        }
    }

    private void isInputSearchTransactionsValid(SearchTransactions searchTransactions) {
        if(searchTransactions == null) {
            throw new SearchServiceException(400, "Bad request. Request body is - null");
        } else if (searchTransactions.getExpensesTransactions().isEmpty()) {
            throw new SearchServiceException(400, "Bad request. Please check request body.");
        }
    }

    private void isUserIdValid(Integer userId) {
       if(userId == null || userId == 0) {
           throw new SearchServiceException(400, "User id is Invalid - " + userId);
       }
    }
}
