package com.finance.expensesservice.service;

import com.finance.common.context.UserContext;
import com.finance.common.dto.Transaction;
import com.finance.common.exception.ServiceException;
import com.finance.expensesservice.client.SearchAPIClient;
import com.finance.expensesservice.dao.TransactionDAO;
import com.finance.expensesservice.dao.util.TransactionSpecifications;
import com.finance.expensesservice.domain.Category;
import com.finance.common.dto.SearchTransactions;
import com.finance.expensesservice.domain.ExpensesTransaction;
import com.finance.expensesservice.exception.ObjectNotFoundException;
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

import static com.finance.expensesservice.util.Util.cast;

/**
 * Created by siarh on 07/05/2017.
 */
@Service
public class TransactionServiceImpl implements TransactionService, ApplicationContextAware {

    private final static Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionDAO transactionDAO;

    private final CategoryService categoryService;

    private final UserContext userContext;

    private final SearchAPIClient searchAPIClient;

    @Value("${expenses.number.search.attempts}")
    private Integer numberOfSearchAttempts;

    private ApplicationContext context;

    @Autowired
    public TransactionServiceImpl(TransactionDAO transactionDAO,
                                  CategoryService categoryService,
                                  UserContext userContext,
                                  SearchAPIClient searchAPIClient,
                                  ApplicationContext context) {

        this.transactionDAO = transactionDAO;
        this.categoryService = categoryService;
        this.userContext = userContext;
        this.searchAPIClient = searchAPIClient;
        this.context = context;
    }

    @Override
    @Transactional
    public void uploadData(MultipartFile file) throws ServiceException {
        if(file != null && !file.isEmpty()) {
            List<ExpensesTransaction> expensesTransactions;
            try {
                expensesTransactions =  new ReaderContext(StatementReaderFactory.readerStrategy(file.getOriginalFilename()))
                        .readTransactionsFromFile(file.getInputStream());
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Can't read expensesTransactions from file "
                        + file.getOriginalFilename());
            }

            Category defaultCategory = categoryService.findDefaultCategory(true);
            List<ExpensesTransaction> oldLastDateExpensesTransactions = transactionDAO.findLastDateTransactions(userContext.getUserId());

            saveTransactions(oldLastDateExpensesTransactions, expensesTransactions, defaultCategory);
        }
    }

    @Override
    @Transactional
    public void deleteAllTransactions() {
        transactionDAO.deleteAllTransactions(userContext.getUserId());
        searchAPIClient.deleteAllUserTransactionsFromIndex(userContext.getUserId());
    }

    @Override
    public List<ExpensesTransaction> findTransactions(Integer categoryId, String order) {
        return transactionDAO.findAll(TransactionSpecifications.getTransactions(userContext.getUserId(), categoryId, order));
    }

    @Override
    @Transactional
    public void addTransactionsToCategory(Integer categoryId, List<Integer> transactionsIds) throws ServiceException {
        Category category = categoryService.findCategoryById(categoryId, true);
        if(category == null) {
            throw new ObjectNotFoundException("Category with id " + categoryId + " doesn't exist for user " + userContext.getUserId());
        }

        if(CollectionUtils.isEmpty(transactionsIds)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "ExpensesTransaction ids is " + transactionsIds);
        }

        List<ExpensesTransaction> expensesTransactions = transactionDAO.findTransactionsByIds(userContext.getUserId(), transactionsIds);

        if(saveTransactionsToCategory(category, expensesTransactions))
            searchAPIClient.updateTransactionsIndex(userContext.getUserId(), new SearchTransactions(category.getId(), cast(expensesTransactions)));
    }

    @Override
    public void searchAndAddToCategory(String desc, Integer categoryId, Integer categoryToAddId) {
        if(StringUtils.isEmpty(desc)) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Description can't be empty.");
        }

        Category category = categoryService.findCategoryById(categoryId, false);
        if(category == null) {
            throw new ObjectNotFoundException("Category with id " + categoryId + " doesn't exist for user " + userContext.getUserId());
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
        List<ExpensesTransaction> result = searchAPIClient.searchTransactions(userContext.getUserId(), categoryId, desc);
        return CollectionUtils.isEmpty(result) ? Collections.emptyList() :
                result.stream()
                        .map(Transaction :: getId)
                        .collect(Collectors.toList());
    }

    private boolean saveTransactionsToCategory(Category category, List<ExpensesTransaction> expensesTransactions) throws ServiceException {
        if(!CollectionUtils.isEmpty(expensesTransactions) && category != null) {
            category.addAll(expensesTransactions);
            categoryService.update(category);
            logger.info("{} expensesTransactions for user with id {} have been added to category {}",
                    expensesTransactions.size(), userContext.getUserId(), category.getId());
            return true;
        }

        return false;
    }

    private void saveTransactions(List<ExpensesTransaction> oldExpensesTransactions, List<ExpensesTransaction> newExpensesTransactions, Category category) {
        List<ExpensesTransaction> transactionsToAdd = prepareTransactionsToAdd(oldExpensesTransactions, newExpensesTransactions);
        logger.info("Going to add {} transactions of {} to db", transactionsToAdd.size(), newExpensesTransactions.size());
        if(!CollectionUtils.isEmpty(transactionsToAdd) && saveTransactionsToCategory(category, transactionsToAdd)) {
            searchAPIClient.indexTransactions(userContext.getUserId(), new SearchTransactions(category.getId(), cast(findTransactions(category.getId(), null))));
        }
    }

    private List<ExpensesTransaction> prepareTransactionsToAdd(List<ExpensesTransaction> oldExpensesTransactions, List<ExpensesTransaction> newExpensesTransactions) {
        if (CollectionUtils.isEmpty(oldExpensesTransactions)){
            return CollectionUtils.isEmpty(newExpensesTransactions) ? Collections.emptyList() : newExpensesTransactions;
        }

        newExpensesTransactions.sort(Comparator.comparing(ExpensesTransaction::getTransactionDate));

        ExpensesTransaction lastOldExpensesTransaction = getFirstTransaction(oldExpensesTransactions);
        ExpensesTransaction firstNewExpensesTransaction = getFirstTransaction(newExpensesTransactions);

        if(lastOldExpensesTransaction == null || firstNewExpensesTransaction == null) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error merging of transactions");
        }

        return lastOldExpensesTransaction.getTransactionDate().before(firstNewExpensesTransaction.getTransactionDate()) ? newExpensesTransactions :
                mergeTransactionsToAdd(newExpensesTransactions, oldExpensesTransactions, lastOldExpensesTransaction.getTransactionDate(),
                        lastOldExpensesTransaction.getTransactionDate().equals(firstNewExpensesTransaction.getTransactionDate()) ?
                                firstNewExpensesTransaction.getTransactionDate() : lastOldExpensesTransaction.getTransactionDate());
    }

    private List<ExpensesTransaction> mergeTransactionsToAdd(List<ExpensesTransaction> newExpensesTransactions,
                                                             List<ExpensesTransaction> oldExpensesTransactions,
                                                             Timestamp oldTransactionEndDate,
                                                             Timestamp newTransactionsStartDate) {

        List<ExpensesTransaction> transactionsToAdd = merge(filterTransactionsByDate(oldExpensesTransactions, oldTransactionEndDate),
                filterTransactionsByDate(newExpensesTransactions, newTransactionsStartDate));

        transactionsToAdd.addAll(excludeTransactionsByDate(newExpensesTransactions, newTransactionsStartDate));

        return transactionsToAdd;
    }

    private List<ExpensesTransaction> merge(List<ExpensesTransaction> oldLastDayExpensesTransactions, List<ExpensesTransaction> newFirstDayExpensesTransactions) {
        List<ExpensesTransaction> resultExpensesTransactions = new ArrayList<>();
        if(newFirstDayExpensesTransactions.size() > oldLastDayExpensesTransactions.size()) {
            resultExpensesTransactions.addAll(newFirstDayExpensesTransactions.stream()
                    .filter(v -> !isTransactionInTransactionList(oldLastDayExpensesTransactions, v))
                    .collect(Collectors.toList()));
        }

        return resultExpensesTransactions;
    }

    private List<ExpensesTransaction> excludeTransactionsByDate(List<ExpensesTransaction> expensesTransactions, Timestamp date) {
        return CollectionUtils.isEmpty(expensesTransactions) || date == null ? expensesTransactions :
                expensesTransactions.stream()
                        .filter(v -> !v.getTransactionDate().equals(date) && v.getTransactionDate().after(date))
                        .collect(Collectors.toList());
    }

    private ExpensesTransaction getFirstTransaction(List<ExpensesTransaction> expensesTransactions) {
        return CollectionUtils.isEmpty(expensesTransactions) ? null : expensesTransactions.get(0);
    }

    private List<ExpensesTransaction> filterTransactionsByDate(List<ExpensesTransaction> expensesTransactions, Timestamp date) {
        return CollectionUtils.isEmpty(expensesTransactions) ||  date == null ? Collections.emptyList() :
                expensesTransactions.stream()
                        .filter(v -> v.getTransactionDate().equals(date))
                        .collect(Collectors.toList());
    }

    private boolean isTransactionInTransactionList(List<ExpensesTransaction> list, ExpensesTransaction expensesTransaction) {
        return !CollectionUtils.isEmpty(list) && expensesTransaction.getTransactionDate() != null
                && list.stream().anyMatch(tr -> expensesTransaction.getTransactionDate().equals(tr.getTransactionDate())
                && expensesTransaction.getDescription().equals(tr.getDescription()) && compareAmounts(expensesTransaction.getAmount(), tr.getAmount()));
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

    /**
     * Transaction annotation(aspect) only works on proxy objects.
     * If try to execute @Transactional method from
     * another method spring calls this.method instate of proxy.method
     * @return Spring proxy object of TransactionServiceImpl
     */
    private TransactionServiceImpl getProxy() {
        return context.getBean(this.getClass());
    }
}
