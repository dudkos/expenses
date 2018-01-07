package com.finance.common.dto;
import java.util.List;

public class SearchTransactions {

    private Integer categoryId;

    private List<Transaction> transactions;

    public SearchTransactions() {
    }

    public SearchTransactions(Integer categoryId, List<Transaction> transactions) {
        this.categoryId = categoryId;
        this.transactions = transactions;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
}
