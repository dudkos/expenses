package com.finance.expensesservice.util.calculation;

import com.finance.expensesservice.domain.Transaction;
import com.finance.expensesservice.dto.TransactionResult;
import com.finance.expensesservice.exception.ExpensesServiceException;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

public abstract class CalculationTemplate {

    abstract String getPeriodValue(Calendar calendar);

    public final List<TransactionResult> transactionResult(List<Transaction> transactions) {
        List<TransactionResult> transactionResults = new ArrayList<>();
        transactions.forEach(v -> updateTransactionResult(transactionResults, v));
        calculateTotal(transactionResults);

        return transactionResults;
    }

    private void updateTransactionResult(List<TransactionResult> results, Transaction transaction) {
        addToTransactionResult(getTransactionResultByPeriod(results, getPeriodPointFromTransaction(transaction)), transaction);
    }

    private TransactionResult getTransactionResultByPeriod(List<TransactionResult> results, String period) {
        return results.stream()
                .filter(result -> result.getPeriod().equals(period)).findFirst()
                .orElseGet(() -> newTransactionResult(results, period));
    }

    private TransactionResult newTransactionResult(List<TransactionResult> results, String period) {
        TransactionResult transactionResult = new TransactionResult();
        transactionResult.setPeriod(period);
        results.add(transactionResult);
        return transactionResult;
    }

    private String getPeriodPointFromTransaction(Transaction transaction) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(transaction.getTransactionDate());
        return getPeriodValue(calendar);
    }

    private static void  addToTransactionResult(TransactionResult result, Transaction transaction) throws ExpensesServiceException {
        if(result == null && transaction == null) {
            throw new ExpensesServiceException(HttpStatus.BAD_REQUEST.value(), "transaction result or transaction is null");
        }
        if(transaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            assert result != null;
            result.setTotalCreditAmount(result.getTotalCreditAmount().add(transaction.getAmount().abs()));
        } else {
            assert result != null;
            result.setTotalDebitAmount(result.getTotalDebitAmount().add(transaction.getAmount()));
        }
    }

    private void calculateTotal(List<TransactionResult> transactionResults) {
        if(!CollectionUtils.isEmpty(transactionResults)) transactionResults
                .forEach(v-> v.setResult(v.getTotalCreditAmount().negate().add(v.getTotalDebitAmount())));
    }
}
