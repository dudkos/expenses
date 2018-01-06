package com.finance.expensesservice.dto;

import com.finance.expensesservice.domain.ExpensesTransaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by siarh on 30/04/2017.
 */
public class TransactionResult {

    private String period;

    private BigDecimal totalDebitAmount = new BigDecimal(0);

    private BigDecimal totalCreditAmount = new BigDecimal(0);

    private BigDecimal result;

    private List<ExpensesTransaction> expensesTransactions = new ArrayList<>();

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public BigDecimal getTotalDebitAmount() {
        return totalDebitAmount;
    }

    public void setTotalDebitAmount(BigDecimal totalDebitAmount) {
        this.totalDebitAmount = totalDebitAmount;
    }

    public BigDecimal getTotalCreditAmount() {
        return totalCreditAmount;
    }

    public void setTotalCreditAmount(BigDecimal totalCreditAmount) {
        this.totalCreditAmount = totalCreditAmount;
    }

    public BigDecimal getResult() {
        return result;
    }

    public void setResult(BigDecimal result) {
        this.result = result;
    }

    public List<ExpensesTransaction> getExpensesTransactions() {
        return expensesTransactions;
    }

    public void setExpensesTransactions(List<ExpensesTransaction> expensesTransactions) {
        this.expensesTransactions = expensesTransactions;
    }
}
