package com.finance.expensesservice.domain;

import java.util.ArrayList;
import java.util.List;

public class SearchTransactions {

    private Integer categoryId;

    private List<Transaction> expensesTransactions = new ArrayList<>();

    public SearchTransactions(Integer categoryId, List<Transaction> expensesTransactions) {
        this.categoryId = categoryId;
        this.expensesTransactions = expensesTransactions;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<Transaction> getExpensesTransactions() {
        return expensesTransactions;
    }

    public void setExpensesTransactions(List<Transaction> expensesTransactions) {
        this.expensesTransactions = expensesTransactions;
    }
}
