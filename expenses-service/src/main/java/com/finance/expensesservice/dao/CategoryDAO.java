package com.finance.expensesservice.dao;

import com.finance.expensesservice.domain.Category;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by siarh on 06/05/2017.
 */
@Repository
public interface CategoryDAO extends CrudRepository<Category, Integer> {

    @Query("SELECT c FROM Category c WHERE c.userId =:userId and c.id =:categoryId")
    Category findCategoryById(@Param("userId") Integer userId, @Param("categoryId") Integer categoryId);

    @Query("SELECT DISTINCT c FROM Category c LEFT OUTER JOIN FETCH c.expensesTransactions as t  WHERE c.userId =:userId and c.id =:categoryId")
    Category findCategoryByIdWithTr(@Param("userId") Integer userId, @Param("categoryId") Integer categoryId);

    @Query("SELECT c FROM Category c WHERE c.userId =:userId and c.name =:name")
    Category findCategoryByName(@Param("userId") Integer userId, @Param("name") String name);

    @Query("SELECT c FROM Category c LEFT OUTER JOIN FETCH c.expensesTransactions as t WHERE c.userId =:userId and c.name =:name")
    Category findCategoryByNameWithTr(@Param("userId") Integer userId, @Param("name") String name);

    @Query("SELECT c FROM Category c WHERE c.userId =:userId ORDER BY c.lastModification ASC")
    List<Category> findCategories(@Param("userId") Integer userId);
}
