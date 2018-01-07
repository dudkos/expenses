package com.finanse.search.api.controller;

import com.finance.common.context.UserContext;
import com.finance.common.dto.SearchTransactions;
import com.finance.common.dto.Transaction;
import com.finanse.search.api.service.TransactionsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions/")
public class TransactionsSearchController {

    @Value("${gui.search.result.size}")
    private Integer guiSearchResultSize;

    @Value("${server.search.result.size}")
    private Integer serverSearchResultSize;

    private TransactionsSearchService transactionsSearchService;

    private UserContext userContext;

    @Autowired
    public TransactionsSearchController(TransactionsSearchService transactionsSearchService,
                                        UserContext userContext) {
        this.transactionsSearchService = transactionsSearchService;
        this.userContext = userContext;
    }

    @PreAuthorize("#oauth2.hasAnyScope('gui')")
    @RequestMapping(value = "categories/{categoryId}/search", method = RequestMethod.GET)
    public List<Transaction> searchTransactions(@PathVariable Integer categoryId,
                                                @RequestParam(value = "description") String description) {
        return transactionsSearchService.searchTransaction(userContext.getUserId(), categoryId, description, guiSearchResultSize);
    }

    @PreAuthorize("#oauth2.hasAnyScope('server')")
    @RequestMapping(value = "{userId}/categories/{categoryId}/search", method = RequestMethod.GET)
    public List<Transaction> searchTransactions(@PathVariable Integer userId,
                                                @PathVariable Integer categoryId,
                                                @RequestParam(value = "description") String description) {
        return transactionsSearchService.searchTransaction(userId, categoryId, description, serverSearchResultSize);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "{userId}/index", method = RequestMethod.PUT)
    public ResponseEntity<Void> indexTransactions(@PathVariable Integer userId,
                                                  @RequestBody SearchTransactions searchTransactions) {
        transactionsSearchService.indexTransactions(userId, searchTransactions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "{userId}/update", method = RequestMethod.PUT)
    public ResponseEntity<Void> updateTransactionsIndex(@PathVariable Integer userId,
                                                        @RequestBody SearchTransactions searchTransactions) {
        transactionsSearchService.updateTransactions(userId, searchTransactions);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "{userId}/delete", method = RequestMethod.DELETE)
    public ResponseEntity<Void> deleteAllTransactionsFromIndex(@PathVariable Integer userId) {
        transactionsSearchService.deleteFromIndexByUserId(userId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
