package com.finance.expensesservice.service;

import com.finance.expensesservice.client.SearchAPIClient;
import com.finance.expensesservice.dao.CategoryDAO;
import com.finance.expensesservice.domain.Category;
import com.finance.expensesservice.domain.SearchTransactions;
import com.finance.expensesservice.domain.Transaction;
import com.finance.expensesservice.exception.ExpensesServiceException;
import com.finance.expensesservice.exception.ObjectNotFoundException;
import com.finance.expensesservice.util.security.OAuth2Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.finance.expensesservice.util.ExpensesServiceConstants.DEFAULT_CATEGORY_NAME;

/**
 * Created by siarh on 06/05/2017.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    private final OAuth2Util oAuth2Util;

    private final SearchAPIClient searchAPIClient;

    @Autowired
    public CategoryServiceImpl(CategoryDAO categoryDAO,
                               OAuth2Util oAuth2Util,
                               SearchAPIClient searchAPIClient) {
        this.categoryDAO = categoryDAO;
        this.oAuth2Util = oAuth2Util;
        this.searchAPIClient = searchAPIClient;
    }

    @Override
    public Category findCategoryById(Integer id, boolean withTransactions) {
        Category result =  withTransactions ? categoryDAO.findCategoryByIdWithTr(oAuth2Util.getUserIdFromAuth(), id) :
                categoryDAO.findCategoryById(oAuth2Util.getUserIdFromAuth(), id);

        if(result == null) {
            throw new ObjectNotFoundException("Can't find category with id " + id + " for user " + oAuth2Util.getUserNameFromAuth());
        }

        return result;
    }

    @Override
    public Category findCategoryByName(String name, boolean withTransactions){
        return withTransactions ? categoryDAO.findCategoryByNameWithTr(oAuth2Util.getUserIdFromAuth(), name) :
                 categoryDAO.findCategoryByName(oAuth2Util.getUserIdFromAuth(), name);
    }

    @Override
    public Category findDefaultCategory(boolean withTransactions) throws ExpensesServiceException {
        Category defaultCategory = findCategoryByName(DEFAULT_CATEGORY_NAME, withTransactions);
        if(defaultCategory == null) {
            defaultCategory = createCategory(new Category(DEFAULT_CATEGORY_NAME));
        }

        return defaultCategory;
    }

    @Override
    public List<Category> findCategories() throws ExpensesServiceException {
        List<Category> categories = categoryDAO.findCategories(oAuth2Util.getUserIdFromAuth());
        if(CollectionUtils.isEmpty(categories) || !categories.contains(new Category(DEFAULT_CATEGORY_NAME))) {
            Category defaultCategory = new Category(DEFAULT_CATEGORY_NAME);
            createCategory(new Category(DEFAULT_CATEGORY_NAME));
            return new ArrayList<Category>(){{
                add(defaultCategory);
            }};
        }

        return categories;
    }

    @Override
    @Transactional
    public Category updateCategory(Integer categoryId, Category category){
        Category fromDb = categoryDAO.findCategoryByIdWithTr(oAuth2Util.getUserIdFromAuth(), categoryId);
        if(fromDb == null) {
            throw new ObjectNotFoundException("can't find category with id " + categoryId + " for user " + oAuth2Util.getUserIdFromAuth());
        }
        if(DEFAULT_CATEGORY_NAME.equals(fromDb.getName())) {
            throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "Can't update default category");
        }
        if(!StringUtils.isEmpty(category.getName())) {
            fromDb.setName(category.getName());
            categoryDAO.save(fromDb);
        }
        return fromDb;
    }

    @Override
    public void update(Category category) {
        categoryDAO.save(category);
    }

    @Override
    @Transactional
    public Category createCategory(Category category){
        if(DEFAULT_CATEGORY_NAME.equals(category.getName()) && findCategoryByName(DEFAULT_CATEGORY_NAME, false) != null) {
            throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "Default category already exists");
        }
        Category newCategory = new Category(category);
        newCategory.setUserId(oAuth2Util.getUserIdFromAuth());
        categoryDAO.save(newCategory);

        return newCategory;
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = categoryDAO.findCategoryByIdWithTr(oAuth2Util.getUserIdFromAuth(), categoryId);

        if(category == null) {
            throw new ObjectNotFoundException("Can't find category with id " + categoryId);
        }

        if(DEFAULT_CATEGORY_NAME.equals(category.getName())) {
            throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "Can't delete default category");
        }

        Set<Transaction> transactionsToMove = category.getTransactions();

        if(!CollectionUtils.isEmpty(transactionsToMove)) {
            List<Transaction> transactions = new ArrayList<>();
            transactions.addAll(transactionsToMove);
            Category defaultCategory = findDefaultCategory(true);
            defaultCategory.addAll(transactions);
            categoryDAO.save(defaultCategory);
            searchAPIClient.updateTransactionsIndex(new SearchTransactions(defaultCategory.getId(), transactions));
        }

        categoryDAO.delete(category);
    }
}
