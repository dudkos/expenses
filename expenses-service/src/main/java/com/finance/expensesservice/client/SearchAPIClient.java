package com.finance.expensesservice.client;

import com.finance.common.dto.SearchTransactions;
import com.finance.common.dto.Transaction;
import com.finance.expensesservice.domain.ExpensesTransaction;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "search-api", url = "http://localhost:8180/gateway/search")
public interface SearchAPIClient {

    @DeleteMapping(value = "/transactions/delete")
    void deleteAllUserTransactionsFromIndex();

    @PutMapping(value = "/transactions/index")
    void indexTransactions(@RequestBody SearchTransactions searchTransactions);

    @GetMapping(value = "/transactions/categories/{categoryId}/search")
    List<ExpensesTransaction> searchTransactions(@PathVariable(name = "categoryId") Integer categoryId,
                                                             @RequestParam(value = "description") String description,
                                                             @RequestParam(value = "size") Integer size);

    @PutMapping(value = "/transactions/update")
    void updateTransactionsIndex(@RequestBody SearchTransactions searchTransactions);
}
