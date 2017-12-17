package com.finance.expensesservice.service;

import com.finance.expensesservice.dto.Chart;
import com.finance.expensesservice.dto.TransactionResult;

import java.util.List;

public interface ExpensesService {

    List<TransactionResult> getTransactionsCalculation(Integer categoryId, String order, String period);

    Chart getChart(Integer categoryId, String order, String period);
}
