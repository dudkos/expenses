package com.finanse.search.api.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class ExpensesTransaction {

    private Integer id;

    private String accountNumber;

    private String currencyCode;

    private Timestamp transactionDate;

    private BigDecimal amount;

    private String description;

    public ExpensesTransaction() {
    }

    public ExpensesTransaction(Integer id,
                               String accountNumber,
                               String currencyCode,
                               Timestamp transactionDate,
                               BigDecimal amount,
                               String description) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.currencyCode = currencyCode;
        this.transactionDate = transactionDate;
        this.amount = amount;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public Timestamp getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
