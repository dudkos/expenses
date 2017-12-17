package com.finance.expensesservice.service;

import com.finance.expensesservice.client.SearchAPIClient;
import com.finance.expensesservice.dao.TransactionDAO;
import com.finance.expensesservice.dao.util.TransactionSpecifications;
import com.finance.expensesservice.domain.Category;
import com.finance.expensesservice.domain.SearchTransactions;
import com.finance.expensesservice.domain.Transaction;
import com.finance.expensesservice.exception.ExpensesServiceException;
import com.finance.expensesservice.exception.ObjectNotFoundException;
import com.finance.expensesservice.util.security.OAuth2Util;
import com.finance.expensesservice.util.reader.ReaderContext;
import com.finance.expensesservice.util.reader.StatementReaderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by siarh on 07/05/2017.
 */
@Service
public class TransactionServiceImpl implements TransactionService, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionDAO transactionDAO;

    private final CategoryService categoryService;

    private final OAuth2Util oAuth2Util;

    private final SearchAPIClient searchAPIClient;

    @Value("${expenses.number.search.attempts}")
    private Integer numberOfSearchAttempts;

    private ApplicationContext context;

    @Autowired
    public TransactionServiceImpl(TransactionDAO transactionDAO,
                                  CategoryService categoryService,
                                  OAuth2Util oAuth2Util,
                                  SearchAPIClient searchAPIClient,
                                  ApplicationContext context) {

        this.transactionDAO = transactionDAO;
        this.categoryService = categoryService;
        this.oAuth2Util = oAuth2Util;
        this.searchAPIClient = searchAPIClient;
        this.context = context;
    }

    @Override
    @Transactional
    public void uploadData(MultipartFile file) throws ExpensesServiceException {
        if(file != null && !file.isEmpty()) {
            List<Transaction> transactions;
            try {
                transactions =  new ReaderContext(StatementReaderFactory.readerStrategy(file.getOriginalFilename()))
                        .readTransactionsFromFile(file.getInputStream());
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "Can't read transactions from file "
                        + file.getOriginalFilename());
            }

            Category defaultCategory = categoryService.findDefaultCategory(true);
            List<Transaction> oldLastDateTransactions = transactionDAO.findLastDateTransactions(oAuth2Util.getUserIdFromAuth());

            saveTransactions(oldLastDateTransactions, transactions, defaultCategory);
        }
    }

    @Override
    @Transactional
    public void deleteAllTransactions() {
        transactionDAO.deleteAllTransactions(oAuth2Util.getUserIdFromAuth());
        searchAPIClient.deleteAllUserTransactionsFromIndex();
    }

    @Override
    public List<Transaction> findTransactions(Integer categoryId, String order) {
        return transactionDAO.findAll(TransactionSpecifications.getTransactions(oAuth2Util.getUserIdFromAuth(), categoryId, order));
    }

    @Override
    @Transactional
    public void addTransactionsToCategory(Integer categoryId, List<Integer> transactionsIds) throws ExpensesServiceException {
        Category category = categoryService.findCategoryById(categoryId, true);
        if(category == null) {
            throw new ObjectNotFoundException("Category with id " + categoryId + " doesn't exist for user " + oAuth2Util.getUserIdFromAuth());
        }

        if(CollectionUtils.isEmpty(transactionsIds)) {
            throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "Transaction ids is " + transactionsIds);
        }

        List<Transaction> transactions = transactionDAO.findTransactionsByIds(oAuth2Util.getUserIdFromAuth(), transactionsIds);

        if(saveTransactionsToCategory(category, transactions))
            searchAPIClient.updateTransactionsIndex(new SearchTransactions(category.getId(), transactions));
    }

    @Override
    public void searchAndAddToCategory(String desc, Integer categoryId, Integer categoryToAddId) {
        if(StringUtils.isEmpty(desc)) {
            throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "Description can't be empty.");
        }

        Category category = categoryService.findCategoryById(categoryId, false);
        if(category == null) {
            throw new ObjectNotFoundException("Category with id " + categoryId + " doesn't exist for user " + oAuth2Util.getUserIdFromAuth());
        }

        int i = 0;
        while (i < numberOfSearchAttempts) {
            List<Integer> foundIds = findTransactionsIdsFromIndex(desc, categoryId);
            if(CollectionUtils.isEmpty(foundIds)) return;
            getProxy().addTransactionsToCategory(categoryToAddId, foundIds);
            i++;
        }
    }

    private List<Integer> findTransactionsIdsFromIndex(String desc, Integer categoryId) {
        List<Transaction> result = searchAPIClient.searchTransactions(categoryId, desc, 100);
        return CollectionUtils.isEmpty(result) ? Collections.emptyList() :
                result.stream()
                        .map(Transaction :: getId)
                        .collect(Collectors.toList());
    }

    private boolean saveTransactionsToCategory(Category category, List<Transaction> transactions) throws ExpensesServiceException {
        if(!CollectionUtils.isEmpty(transactions) && category != null) {
            category.addAll(transactions);
            categoryService.update(category);
            logger.info("{} transactions for user with id {} have been added to category {}",
                    transactions.size(), oAuth2Util.getUserIdFromAuth(), category.getId());
            return true;
        }

        return false;
    }

    private void saveTransactions(List<Transaction> oldTransactions, List<Transaction> newTransactions, Category category) {
        List<Transaction> transactionsToAdd = prepareTransactionsToAdd(oldTransactions, newTransactions);
        logger.info("Going to add {} transactions of {} to db", transactionsToAdd.size(), newTransactions.size());
        if(!CollectionUtils.isEmpty(transactionsToAdd) && saveTransactionsToCategory(category, transactionsToAdd)) {
            searchAPIClient.indexTransactions(new SearchTransactions(category.getId(), findTransactions(category.getId(), null)));
        }
    }

    private List<Transaction> prepareTransactionsToAdd(List<Transaction> oldTransactions, List<Transaction> newTransactions) {
        if (CollectionUtils.isEmpty(oldTransactions)){
            return CollectionUtils.isEmpty(newTransactions) ? Collections.emptyList() : newTransactions;
        }

        newTransactions.sort(Comparator.comparing(Transaction::getTransactionDate));

        Transaction lastOldTransaction = getFirstTransaction(oldTransactions);
        Transaction firstNewTransaction = getFirstTransaction(newTransactions);

        if(lastOldTransaction == null || firstNewTransaction == null) {
            throw new ExpensesServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error merging of transactions");
        }

        return lastOldTransaction.getTransactionDate().before(firstNewTransaction.getTransactionDate()) ? newTransactions :
                mergeTransactionsToAdd(newTransactions, oldTransactions, lastOldTransaction.getTransactionDate(),
                        lastOldTransaction.getTransactionDate().equals(firstNewTransaction.getTransactionDate()) ?
                                firstNewTransaction.getTransactionDate() : lastOldTransaction.getTransactionDate());
    }

    private List<Transaction> mergeTransactionsToAdd(List<Transaction> newTransactions,
                                                     List<Transaction> oldTransactions,
                                                     Timestamp oldTransactionEndDate,
                                                     Timestamp newTransactionsStartDate) {

        List<Transaction> transactionsToAdd = merge(filterTransactionsByDate(oldTransactions, oldTransactionEndDate),
                filterTransactionsByDate(newTransactions, newTransactionsStartDate));

        transactionsToAdd.addAll(excludeTransactionsByDate(newTransactions, newTransactionsStartDate));

        return transactionsToAdd;
    }

    private List<Transaction> merge(List<Transaction> oldLastDayTransactions, List<Transaction> newFirstDayTransactions) {
        List<Transaction> resultTransactions = new ArrayList<>();
        if(newFirstDayTransactions.size() > oldLastDayTransactions.size()) {
            resultTransactions.addAll(newFirstDayTransactions.stream()
                    .filter(v -> !isTransactionInTransactionList(oldLastDayTransactions, v))
                    .collect(Collectors.toList()));
        }

        return resultTransactions;
    }

    private List<Transaction> excludeTransactionsByDate(List<Transaction> transactions, Timestamp date) {
        return CollectionUtils.isEmpty(transactions) || date == null ? transactions :
                transactions.stream()
                        .filter(v -> !v.getTransactionDate().equals(date) && v.getTransactionDate().after(date))
                        .collect(Collectors.toList());
    }

    private Transaction getFirstTransaction(List<Transaction> transactions) {
        return CollectionUtils.isEmpty(transactions) ? null : transactions.get(0);
    }

    private List<Transaction> filterTransactionsByDate(List<Transaction> transactions, Timestamp date) {
        return CollectionUtils.isEmpty(transactions) ||  date == null ? Collections.emptyList() :
                transactions.stream()
                        .filter(v -> v.getTransactionDate().equals(date))
                        .collect(Collectors.toList());
    }

    private boolean isTransactionInTransactionList(List<Transaction> list, Transaction transaction) {
        return !CollectionUtils.isEmpty(list) && transaction.getTransactionDate() != null
                && list.stream().anyMatch(tr -> transaction.getTransactionDate().equals(tr.getTransactionDate())
                && transaction.getDescription().equals(tr.getDescription()) && compareAmounts(transaction.getAmount(), tr.getAmount()));
    }

    private boolean compareAmounts(BigDecimal a1, BigDecimal a2) {
        return a1 != null &&
                a2 != null &&
                a1.setScale(2, RoundingMode.CEILING).equals(a2.setScale(2, RoundingMode.CEILING));
    }


    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }

    private TransactionServiceImpl getProxy() {
        return context.getBean(this.getClass());
    }
}
