package com.finanse.search.api.controller;

import com.finance.common.context.UserContext;
import com.finance.common.dto.SearchTransactions;
import com.finance.common.dto.Transaction;
import com.finanse.search.api.service.TransactionsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions/")
public class TransactionsSearchController {

    private TransactionsSearchService transactionsSearchService;

    private UserContext userContext;

    @Autowired
    public TransactionsSearchController(TransactionsSearchService transactionsSearchService,
                                        UserContext userContext) {
        this.transactionsSearchService = transactionsSearchService;
        this.userContext = userContext;
    }

    //@PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "categories/{categoryId}/search", method = RequestMethod.GET)
    public List<Transaction> searchTransactions(@PathVariable Integer categoryId,
                                                @RequestParam(value = "description") String description,
                                                @RequestParam(value = "size", required = false) Integer size) {
        return transactionsSearchService.searchTransactions(categoryId, description, size);
    }

    @RequestMapping(value = "/index", method = RequestMethod.PUT)
    public ResponseEntity<Void> indexTransactions(@RequestBody SearchTransactions searchTransactions) {
        transactionsSearchService.indexTransactions(searchTransactions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/update", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateTransactionsIndex(@RequestBody SearchTransactions searchTransactions) {
        transactionsSearchService.updateTransactions(searchTransactions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllTransactionsFromIndex() {
        transactionsSearchService.deleteFromIndexByUserId(userContext.getUserId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")//@PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "ping";
    }
}
