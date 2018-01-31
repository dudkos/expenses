package com.finance.expensesservice.controller;

import com.finance.expensesservice.dto.Chart;
import com.finance.expensesservice.dto.TransactionResult;
import com.finance.expensesservice.service.ExpensesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.finance.expensesservice.util.ExpensesServiceConstants.CalculationPeriod.BY_ALL;
import static com.finance.expensesservice.util.ExpensesServiceConstants.Order.ORDER_ASC;

/**
 * Created by siarh on 20/02/2017.
 */
@RestController
@RequestMapping("/transactions")
public class ExpensesController {

    private final ExpensesService expensesService;

    @Autowired
    public ExpensesController(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @GetMapping(value = "/result")
    public List<TransactionResult> getTransactionsCalculation(@RequestParam(value = "category", required = false) Integer categoryId,
                                                              @RequestParam(value = "period", required = false, defaultValue = BY_ALL) String period) {
        return expensesService.getTransactionsCalculation(categoryId, ORDER_ASC, period);
    }

    @GetMapping(value = "/chart")
    public Chart getChart(@RequestParam(value = "category", required = false) Integer categoryId,
                          @RequestParam(value = "period", required = false, defaultValue = BY_ALL) String period) {
        return expensesService.getChart(categoryId, ORDER_ASC, period);
    }
}
