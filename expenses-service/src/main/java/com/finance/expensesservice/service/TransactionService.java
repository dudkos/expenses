package com.finance.expensesservice.service;

import com.finance.expensesservice.domain.Transaction;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Created by siarh on 07/05/2017.
 */
public interface TransactionService {

    void uploadData(MultipartFile file);

    void deleteAllTransactions();

    List<Transaction> findTransactions(Integer categoryId, String order);

    void addTransactionsToCategory(Integer categoryId, List<Integer> transactionsIds);

    void searchAndAddToCategory(String desc, Integer categoryId, Integer categoryToAddId);
}
