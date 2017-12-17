package com.finanse.search.api.service;

import com.finanse.search.api.model.ExpensesTransaction;
import com.finanse.search.api.model.SearchTransactions;

import java.util.List;

public interface TransactionsSearchService {

    void indexTransactions(SearchTransactions searchTransactions);

    void updateTransactions(SearchTransactions searchTransactions);

    void deleteFromIndexByUserId(Integer userId);

    List<ExpensesTransaction> searchTransactions(Integer categoryId, String desc, Integer size);

    List<ExpensesTransaction> searchTransactions(Integer categoryId, List<Long> ids);
}
