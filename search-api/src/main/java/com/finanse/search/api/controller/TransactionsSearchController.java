package com.finanse.search.api.controller;

import com.finanse.search.api.model.ExpensesTransaction;
import com.finanse.search.api.model.SearchServiceError;
import com.finanse.search.api.model.SearchTransactions;
import com.finanse.search.api.service.TransactionsSearchService;
import com.finanse.search.api.util.OAuth2Util;
import com.finanse.search.api.util.SearchServiceException;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions/")
public class TransactionsSearchController {

    private final static Logger logger = LoggerFactory.getLogger(TransactionsSearchController.class);

    private TransactionsSearchService transactionsSearchService;

    private OAuth2Util oAuth2Util;

    @Autowired
    public TransactionsSearchController(TransactionsSearchService transactionsSearchService,
                                        OAuth2Util oAuth2Util) {
        this.transactionsSearchService = transactionsSearchService;
        this.oAuth2Util = oAuth2Util;
    }

    @RequestMapping(value = "categories/{categoryId}/search", method = RequestMethod.GET)
    public List<ExpensesTransaction> searchTransactions(@PathVariable Integer categoryId,
                                                        @RequestParam(value = "description") String description,
                                                        @RequestParam(value = "size", required = false) Integer size) {
        return transactionsSearchService.searchTransactions(categoryId, description, size);
    }

//    @RequestMapping(value = "categories/{categoryId}/search", method = RequestMethod.GET)
//    public List<ExpensesTransaction> searchTransactions(@PathVariable Integer categoryId,
//                                                        @RequestBody List<Long> ids) {
//        return transactionsSearchService.searchTransactions(categoryId, ids);
//    }

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
        transactionsSearchService.deleteFromIndexByUserId(oAuth2Util.getUserIdFromAuth());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('ADMIN')")//@PreAuthorize("#oauth2.hasScope('server')")
    @RequestMapping(value = "/ping", method = RequestMethod.GET)
    public String ping() {
        return "ping";
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<SearchServiceError> handleException(Exception e) {
        if(e instanceof SearchServiceException) {
            return new ResponseEntity<>(new SearchServiceError(((SearchServiceException)e).getCode(), e.getMessage()), HttpStatus.valueOf(((SearchServiceException)e).getCode()));
        } else if(e instanceof NoNodeAvailableException) {
            return new ResponseEntity<>(new SearchServiceError(503, "Search service is not available. Please try later."), HttpStatus.SERVICE_UNAVAILABLE);
        }
        logger.error(e.getMessage());

        return new ResponseEntity<>(new SearchServiceError(500, "Internal service error"), HttpStatus.valueOf(500));
    }
}
