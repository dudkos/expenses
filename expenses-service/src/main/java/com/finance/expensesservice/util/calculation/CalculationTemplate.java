package com.finance.expensesservice.util.calculation;

import com.finance.common.exception.ServiceException;
import com.finance.expensesservice.domain.ExpensesTransaction;
import com.finance.expensesservice.dto.TransactionResult;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class CalculationTemplate {

    abstract String getPeriodValue(Calendar calendar);

    public final List<TransactionResult> transactionResult(List<ExpensesTransaction> expensesTransactions) {
        List<TransactionResult> transactionResults = new ArrayList<>();
        expensesTransactions.forEach(v -> updateTransactionResult(transactionResults, v));
        calculateTotal(transactionResults);

        return transactionResults;
    }

    private void updateTransactionResult(List<TransactionResult> results, ExpensesTransaction expensesTransaction) {
        addToTransactionResult(getTransactionResultByPeriod(results, getPeriodPointFromTransaction(expensesTransaction)), expensesTransaction);
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

    private String getPeriodPointFromTransaction(ExpensesTransaction expensesTransaction) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(expensesTransaction.getTransactionDate());
        return getPeriodValue(calendar);
    }

    private static void  addToTransactionResult(TransactionResult result, ExpensesTransaction expensesTransaction) throws ServiceException {
        if(result == null && expensesTransaction == null) {
            throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "expensesTransaction result or expensesTransaction is null");
        }
        if(expensesTransaction.getAmount().compareTo(BigDecimal.ZERO) < 0) {
            assert result != null;
            result.setTotalCreditAmount(result.getTotalCreditAmount().add(expensesTransaction.getAmount().abs()));
        } else {
            assert result != null;
            result.setTotalDebitAmount(result.getTotalDebitAmount().add(expensesTransaction.getAmount()));
        }
    }

    private void calculateTotal(List<TransactionResult> transactionResults) {
        if(!CollectionUtils.isEmpty(transactionResults)) transactionResults
                .forEach(v-> v.setResult(v.getTotalCreditAmount().negate().add(v.getTotalDebitAmount())));
    }
}
