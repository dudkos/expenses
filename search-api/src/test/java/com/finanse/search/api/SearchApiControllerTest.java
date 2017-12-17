package com.finanse.search.api;

import com.finanse.search.api.model.ExpensesTransaction;
import com.finanse.search.api.model.SearchTransactions;
import com.finanse.search.api.util.SearchServiceException;
import org.junit.Test;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class SearchApiControllerTest {//extends AbstractApiControllerTest {

//    @Test
//    public void searchForTransactions() throws Exception {
//        List<ExpensesTransaction> expensesTransactions = expensesTransactions();
//        given(getTransactionsSearchService().searchTransactions(TEST_CATEGORY_ID, TEST_DESCRIPTION))
//                .willReturn(expensesTransactions);
//        getMockMvc().perform(get("/transactions/users/"+TEST_USER_ID+"/categories/"+TEST_CATEGORY_ID+"/search?description="+TEST_DESCRIPTION).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andExpect(content().json(String.valueOf(getMapper().writeValueAsString(expensesTransactions))));
//    }
//
//    @Test
//    public void searchTransactionsWithWrongParams() throws Exception {
//        List<ExpensesTransaction> expensesTransactions = expensesTransactions();
//        given(getTransactionsSearchService().searchTransactions(TEST_CATEGORY_ID, TEST_DESCRIPTION))
//                .willReturn(expensesTransactions);
//        getMockMvc().perform(get("/transactions/users/1/categories/"+TEST_CATEGORY_ID+"/search?description="+TEST_DESCRIPTION).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andExpect(content().json(String.valueOf(getMapper().writeValueAsString(new ArrayList<>()))));
//        getMockMvc().perform(get("/transactions/users/"+TEST_USER_ID+"/categories/1/search?description="+TEST_DESCRIPTION).accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andExpect(content().json(String.valueOf(getMapper().writeValueAsString(new ArrayList<>()))));
//        getMockMvc().perform(get("/transactions/users/"+TEST_USER_ID+"/categories/1/search?description=").accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk()).andExpect(content().json(String.valueOf(getMapper().writeValueAsString(new ArrayList<>()))));
//    }
//
//    //TODO doThrow doesn't work
//    @Test
//    public void indexTransaction() throws Exception {
//        SearchTransactions searchTransactions = searchTransactions();
//        doThrow(SearchServiceException.class).when(getTransactionsSearchService()).indexTransactions(searchTransactions);
//        getMockMvc().perform(put("/transactions/users/index")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(String.valueOf(getMapper().writeValueAsString(searchTransactions))))
//                .andExpect(status().is(200));
//        getMockMvc().perform(put("/transactions/users/index")
//                .content(String.valueOf(getMapper().writeValueAsString(null))))
//                .andExpect(status().isInternalServerError());
//    }
//
//    @Test
//    public void deleteAllTransactionsFromIndex() throws Exception {
//        getMockMvc().perform(delete("/transactions/users/"+TEST_USER_ID+"/delete")
//                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
//    }
//
//    @Test
//    public void deleteAllTransactionsFromIndexWithWrongParams() throws Exception {
//        getMockMvc().perform(delete("/transactions/users/"+null+"/delete")
//                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isInternalServerError());
//    }
//
//    //TODO doThrow doesn't work
//    @Test
//    public void updateTransactions() throws Exception {
//        SearchTransactions searchTransactions = searchTransactions();
//        doThrow(SearchServiceException.class).when(getTransactionsSearchService()).updateTransactions(searchTransactions);
//        getMockMvc().perform(put("/transactions/users/update")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(getMapper().writeValueAsString(searchTransactions)))
//                .andExpect(status().isOk());
//    }
}
