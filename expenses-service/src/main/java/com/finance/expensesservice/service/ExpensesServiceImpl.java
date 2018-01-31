package com.finance.expensesservice.service;

import com.finance.expensesservice.dto.Chart;
import com.finance.expensesservice.dto.ChartData;
import com.finance.expensesservice.dto.TransactionResult;
import com.finance.expensesservice.util.calculation.CalculationFactory;
import com.finance.expensesservice.util.calculation.CalculationTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by siarh on 22/02/2017.
 */
@Service
public class ExpensesServiceImpl implements ExpensesService {

    private final TransactionService transactionService;

    @Autowired
    public ExpensesServiceImpl(TransactionService expensesTransactionService) {
        this.transactionService = expensesTransactionService;
    }

    @Override
    public List<TransactionResult> getTransactionsCalculation(Integer categoryId, String order, String period) {
        CalculationTemplate calculationTemplate = new CalculationFactory().calculationTemplate(period);
        calculationTemplate.setExpensesTransactions(transactionService.findTransactions(categoryId, order));
        return calculationTemplate.transactionResult();
    }

    @Override
    public Chart getChart(Integer categoryId, String order, String period) {
        List<TransactionResult> expensesTransactions = getTransactionsCalculation(categoryId, order, period);
        List<ChartData> chartData = prepare();
        List<String> labels = new ArrayList<>();
        expensesTransactions.forEach(v -> {
            chartData.get(0).getData().add(v.getTotalDebitAmount());
            chartData.get(1).getData().add(v.getTotalCreditAmount());
            chartData.get(2).getData().add(v.getResult());
            labels.add(v.getPeriod());
        });
        Chart chart = new Chart();
        chart.setChartData(chartData);
        chart.setLabels(labels);

        return chart;
    }

    private List<ChartData> prepare() {
        return new ArrayList<ChartData>(){{
            add(new ChartData(new ArrayList<>(), "Income"));
            add(new ChartData(new ArrayList<>(), "Expenses"));
            add(new ChartData(new ArrayList<>(), "Total"));
        }};





    }
}
