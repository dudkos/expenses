package com.finanse.search.api.service;

import com.finance.common.dto.SearchTransactions;
import com.finance.common.dto.Transaction;

import java.util.List;

public interface TransactionsSearchService {

    void indexTransactions(Integer userId, SearchTransactions searchTransactions);

    void updateTransactions(Integer userId, SearchTransactions searchTransactions);

    void deleteFromIndexByUserId(Integer userId);

    List<Transaction> searchTransaction(Integer userId, Integer categoryId, String desc, Integer size);
}
