package com.finanse.search.api.model;

import java.util.ArrayList;
import java.util.List;

public class SearchTransactions {

    private Integer categoryId;

    private List<ExpensesTransaction> expensesTransactions = new ArrayList<>();

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<ExpensesTransaction> getExpensesTransactions() {
        return expensesTransactions;
    }

    public void setExpensesTransactions(List<ExpensesTransaction> expensesTransactions) {
        this.expensesTransactions = expensesTransactions;
    }
}
