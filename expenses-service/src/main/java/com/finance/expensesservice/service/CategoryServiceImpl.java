package com.finance.expensesservice.service;

import com.finance.common.context.UserContext;
import com.finance.common.exception.ServiceException;
import com.finance.expensesservice.client.SearchAPIClient;
import com.finance.expensesservice.dao.CategoryDAO;
import com.finance.expensesservice.domain.Category;
import com.finance.common.dto.SearchTransactions;
import com.finance.expensesservice.domain.ExpensesTransaction;
import com.finance.expensesservice.exception.ObjectNotFoundException;
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
import static com.finance.expensesservice.util.Util.cast;

/**
 * Created by siarh on 06/05/2017.
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryDAO categoryDAO;

    private final UserContext userContext;

    private final SearchAPIClient searchAPIClient;

    @Autowired
    public CategoryServiceImpl(CategoryDAO categoryDAO,
                               UserContext userContext,
                               SearchAPIClient searchAPIClient) {
        this.categoryDAO = categoryDAO;
        this.userContext = userContext;
        this.searchAPIClient = searchAPIClient;
    }

    @Override
    public Category findCategoryById(Integer id, boolean withTransactions) {
        Category result =  withTransactions ? categoryDAO.findCategoryByIdWithTr(userContext.getUserId(), id) :
                categoryDAO.findCategoryById(userContext.getUserId(), id);

        if(result == null) {
            throw new ObjectNotFoundException("Can't find category with id " + id + " for user " + userContext.getUserName());
        }

        return result;
    }

    @Override
    public Category findCategoryByName(String name, boolean withTransactions){
        return withTransactions ? categoryDAO.findCategoryByNameWithTr(userContext.getUserId(), name) :
                 categoryDAO.findCategoryByName(userContext.getUserId(), name);
    }

    @Override
    public Category findDefaultCategory(boolean withTransactions) throws ServiceException {
        Category defaultCategory = findCategoryByName(DEFAULT_CATEGORY_NAME, withTransactions);
        if(defaultCategory == null) {
            defaultCategory = createCategory(new Category(DEFAULT_CATEGORY_NAME));
        }

        return defaultCategory;
    }

    @Override
    public List<Category> findCategories() throws ServiceException {
        List<Category> categories = categoryDAO.findCategories(userContext.getUserId());
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
        Category fromDb = categoryDAO.findCategoryByIdWithTr(userContext.getUserId(), categoryId);
        if(fromDb == null) {
            throw new ObjectNotFoundException("can't find category with id " + categoryId + " for user " + userContext.getUserId());
        }
        if(DEFAULT_CATEGORY_NAME.equals(fromDb.getName())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Can't update default category");
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
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Default category already exists");
        }
        Category newCategory = new Category(category);
        newCategory.setUserId(userContext.getUserId());
        categoryDAO.save(newCategory);

        return newCategory;
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = categoryDAO.findCategoryByIdWithTr(userContext.getUserId(), categoryId);

        if(category == null) {
            throw new ObjectNotFoundException("Can't find category with id " + categoryId);
        }

        if(DEFAULT_CATEGORY_NAME.equals(category.getName())) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Can't delete default category");
        }

        Set<ExpensesTransaction> transactionsToMove = category.getExpensesTransactions();

        if(!CollectionUtils.isEmpty(transactionsToMove)) {
            List<ExpensesTransaction> expensesTransactions = new ArrayList<>(transactionsToMove);
            Category defaultCategory = findDefaultCategory(true);
            defaultCategory.addAll(expensesTransactions);
            categoryDAO.save(defaultCategory);
            searchAPIClient.updateTransactionsIndex(userContext.getUserId(), new SearchTransactions(defaultCategory.getId(), cast(expensesTransactions)));
        }

        categoryDAO.delete(category);
    }
}
