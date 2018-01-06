package com.finanse.search.api.service;

import com.finance.common.dto.SearchTransactions;
import com.finance.common.dto.Transaction;

import java.util.List;

public interface TransactionsSearchService {

    void indexTransactions(SearchTransactions searchTransactions);

    void updateTransactions(SearchTransactions searchTransactions);

    void deleteFromIndexByUserId(Integer userId);

    List<Transaction> searchTransactions(Integer categoryId, String desc, Integer size);
}
