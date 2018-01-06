package com.finance.expensesservice.dao;

import com.finance.expensesservice.domain.ExpensesTransaction;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by siarh on 6/25/2017.
 */
@Repository
public interface TransactionDAO extends CrudRepository <ExpensesTransaction, Integer>, JpaSpecificationExecutor<ExpensesTransaction> {

    @Modifying
    @Query("DELETE FROM ExpensesTransaction t WHERE t.category.id IN (SELECT id FROM Category c WHERE c.userId =:userId)")
    void deleteAllTransactions(@Param("userId") Integer userId);

    @Modifying
    @Query("DELETE FROM ExpensesTransaction t WHERE t.id IN (:ids)")
    void deleteTransactionsByIds(@Param("ids") List<Long> ids);

    @Query("SELECT DISTINCT t FROM ExpensesTransaction t LEFT OUTER JOIN t.category AS c WHERE c.userId =:userId AND t.id IN (:ids)")
    List<ExpensesTransaction> findTransactionsByIds(@Param("userId") Integer userId, @Param("ids") List<Integer> ids);

    @Query("SELECT t FROM ExpensesTransaction t LEFT OUTER JOIN t.category AS c " +
            "WHERE c.userId =:userId AND t.transactionDate =" +
            "(SELECT MAX(t.transactionDate) FROM ExpensesTransaction t LEFT OUTER JOIN t.category AS c WHERE c.userId =:userId) " +
            "ORDER BY t.lastModification desc")
    List<ExpensesTransaction> findLastDateTransactions(@Param("userId") Integer userId);
}
