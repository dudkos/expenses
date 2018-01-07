package com.finance.expensesservice.client;

import com.finance.common.dto.SearchTransactions;
import com.finance.expensesservice.domain.ExpensesTransaction;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "search-api", url = "http://localhost:8180/gateway/search")
public interface SearchAPIClient {

    @DeleteMapping(value = "/transactions/{userId}/delete")
    void deleteAllUserTransactionsFromIndex(@PathVariable("userId") Integer userId);

    @PutMapping(value = "/transactions/{userId}/index")
    void indexTransactions(@PathVariable("userId") Integer userId,
                           @RequestBody SearchTransactions searchTransactions);

    @GetMapping(value = "/transactions/{userId}/categories/{categoryId}/search")
    List<ExpensesTransaction> searchTransactions(@PathVariable(name = "userId") Integer userId,
                                                 @PathVariable(name = "categoryId") Integer categoryId,
                                                 @RequestParam(value = "description") String description);

    @PutMapping(value = "/transactions/{userId}/update")
    void updateTransactionsIndex(@PathVariable("userId") Integer userId,
                                 @RequestBody SearchTransactions searchTransactions);
}
