package com.finance.expensesservice.controller;

import com.finance.expensesservice.domain.ExpensesTransaction;
import com.finance.expensesservice.service.TransactionService;
import com.finance.expensesservice.util.ExpensesServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

import static com.finance.expensesservice.util.ExpensesServiceConstants.Order.ORDER_DESC;

/**
 * Created by siarh on 07/05/2017.
 */
@RestController
public class TransactionController implements ExpensesServiceConstants {

    private final TransactionService transactionService;

    @Autowired
    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Void> uploadData(@RequestParam MultipartFile file) {
        transactionService.uploadData(file);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping(value = "/transactions")
    public ResponseEntity<Void> deleteAllTransactions() {
        transactionService.deleteAllTransactions();
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping(value = "/categories/{categoryId}/transactions")
    public List<ExpensesTransaction> findTransactions(@PathVariable Integer categoryId) {
        return transactionService.findTransactions(categoryId, ORDER_DESC);
    }

    @PutMapping(value = "/categories/{categoryId}/transactions")
    public ResponseEntity<Void> putTransactionsToCategory(@PathVariable Integer categoryId,
                                                          @RequestBody List<Integer> transactionIds) {
        transactionService.addTransactionsToCategory(categoryId, transactionIds);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping(value = "/categories/{categoryId}/search")
    public ResponseEntity<Void> searchAndAddToCategory(@RequestParam(value = "desc") String desc,
                                                       @PathVariable Integer categoryId,
                                                       @RequestParam(value = "categoryToAddId") Integer categoryToAddId) {
        transactionService.searchAndAddToCategory(desc, categoryId, categoryToAddId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
