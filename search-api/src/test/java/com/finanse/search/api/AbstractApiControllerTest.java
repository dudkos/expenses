package com.finanse.search.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finanse.search.api.controller.TransactionsSearchController;
import com.finanse.search.api.model.ExpensesTransaction;
import com.finanse.search.api.model.SearchTransactions;
import com.finanse.search.api.service.TransactionsSearchService;
import com.finanse.search.api.util.SearchApiTestsConstants;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@WebMvcTest(TransactionsSearchController.class)
abstract public class AbstractApiControllerTest implements SearchApiTestsConstants{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionsSearchService transactionsSearchService;

    @Autowired
    private ObjectMapper mapper;

    public MockMvc getMockMvc() {
        return mockMvc;
    }

    public TransactionsSearchService getTransactionsSearchService() {
        return transactionsSearchService;
    }

    public ObjectMapper getMapper() {
        return mapper;
    }

    public List<ExpensesTransaction> expensesTransactions() {
        return new ArrayList<ExpensesTransaction>(){{
            add(new ExpensesTransaction(1, "test account", "EUR",
                    new Timestamp(System.currentTimeMillis()), new BigDecimal(100), "test description"));
            add(new ExpensesTransaction(2, "test account", "EUR",
                    new Timestamp(System.currentTimeMillis()), new BigDecimal(200), "test description second"));
        }};
    }

    public SearchTransactions searchTransactions() {
        SearchTransactions searchTransactions = new SearchTransactions();
        //searchTransactions.setUserId(TEST_USER_ID);
        searchTransactions.setCategoryId(TEST_CATEGORY_ID);
        searchTransactions.setExpensesTransactions(expensesTransactions());
        return searchTransactions;
    }
}
