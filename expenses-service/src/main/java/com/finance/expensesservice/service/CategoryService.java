package com.finance.expensesservice.service;

import com.finance.expensesservice.domain.Category;
import com.finance.expensesservice.exception.ExpensesServiceException;

import java.util.List;

/**
 * Created by siarh on 06/05/2017.
 */
public interface CategoryService {

    Category findCategoryById(Integer id, boolean withTransactions) throws ExpensesServiceException;

    Category findCategoryByName(String name, boolean withTransactions) throws ExpensesServiceException;

    Category findDefaultCategory(boolean withTransactions) throws ExpensesServiceException;

    List<Category> findCategories() throws ExpensesServiceException;

    Category updateCategory(Integer categoryId, Category expensesCategory) throws ExpensesServiceException;

    void update(Category expensesCategory);

    Category createCategory(Category expensesCategory) throws ExpensesServiceException;

    void deleteCategory(Integer categoryId) throws ExpensesServiceException;
}
