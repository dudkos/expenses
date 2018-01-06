package com.finance.expensesservice.service;

import com.finance.common.exception.ServiceException;
import com.finance.expensesservice.domain.Category;

import java.util.List;

/**
 * Created by siarh on 06/05/2017.
 */
public interface CategoryService {

    Category findCategoryById(Integer id, boolean withTransactions) throws ServiceException;

    Category findCategoryByName(String name, boolean withTransactions) throws ServiceException;

    Category findDefaultCategory(boolean withTransactions) throws ServiceException;

    List<Category> findCategories() throws ServiceException;

    Category updateCategory(Integer categoryId, Category expensesCategory) throws ServiceException;

    void update(Category expensesCategory);

    Category createCategory(Category expensesCategory) throws ServiceException;

    void deleteCategory(Integer categoryId) throws ServiceException;
}
