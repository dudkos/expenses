package com.finanse.search.api;

import com.finanse.search.api.service.TransactionsSearchService;
import com.finanse.search.api.util.SearchApiTestsConstants;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.Assert.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

//@RunWith(SpringRunner.class)
//@SpringBootTest
public class SearchApiTransactionServiceTests implements SearchApiTestsConstants {

//	@Autowired
//	private TransactionsSearchService transactionsSearchService;
//
//	@Rule
//	public ExpectedException expectedException = ExpectedException.none();
//
//	@Before
//	public void prepare() throws InterruptedException {
//		createTestIndex();
//	}
//
//	//TODO use test ng to avoid static after and before class
//	@After
//	public void clear() {
//		transactionsSearchService.deleteFromIndexByUserId(TEST_USER_ID);
//	}
//
//	@Test
//	public void searchAndUpdate() {
//		List<ExpensesTransaction> expensesTransactions = transactionsSearchService.searchTransactions(TEST_CATEGORY_ID, "tes");
//		assertEquals(expensesTransactions.size(), 2);
//		SearchTransactions searchTransactions = new SearchTransactions();
//		searchTransactions.setExpensesTransactions(expensesTransactions);
////		searchTransactions.setUserId(TEST_USER_ID);
//		searchTransactions.setCategoryId(TEST_SECOND_CATEGORY_iD);
//		transactionsSearchService.updateTransactions(searchTransactions);
//		addDelay(1000);
//		assertEquals(transactionsSearchService.searchTransactions(TEST_CATEGORY_ID," test").isEmpty(), true);
//		assertEquals(transactionsSearchService.searchTransactions(TEST_SECOND_CATEGORY_iD," test").size(), 2);
//	}
//
//	@Test
//	public void indexInvalidUserId() {
//		setExpectedException("User id is Invalid - null");
//		SearchTransactions searchTransactions = prepareSearchTransactions();
////		searchTransactions.setUserId(null);
//		transactionsSearchService.indexTransactions(searchTransactions);
//	}
//
//	@Test
//	public void indexInvalidCategoryId() {
//		setExpectedException("Category id is Invalid - null");
//		SearchTransactions searchTransactions = prepareSearchTransactions();
//		searchTransactions.setCategoryId(null);
//		transactionsSearchService.indexTransactions(searchTransactions);
//	}
//
//	@Test
//	public void indexInvalidBody() {
//		setExpectedException("Bad request. Request body is - null");
//		transactionsSearchService.indexTransactions(null);
//	}
//
//	@Test
//	public void updateIndexInvalidUserId() {
//		setExpectedException("User id is Invalid - null");
//		SearchTransactions searchTransactions = prepareSearchTransactions();
////		searchTransactions.setUserId(null);
//		transactionsSearchService.updateTransactions(searchTransactions);
//	}
//
//	@Test
//	public void updateIndexInvalidCategoryId() {
//		setExpectedException("Category id is Invalid - null");
//		SearchTransactions searchTransactions = prepareSearchTransactions();
//		searchTransactions.setCategoryId(null);
//		transactionsSearchService.updateTransactions(searchTransactions);
//	}
//
//	@Test
//	public void updateInvalidBody() {
//		setExpectedException("Bad request. Request body is - null");
//		transactionsSearchService.updateTransactions(null);
//	}
//
//	@Test
//	public void searchInvalidUserId() {
//		assertEquals(transactionsSearchService.searchTransactions(TEST_CATEGORY_ID, "tes").isEmpty(), true);
//	}
//
//	@Test
//	public void searchInvalidCategoryId() {
//		assertEquals(transactionsSearchService.searchTransactions(0, "tes").isEmpty(), true);
//	}
//
//	@Test
//	public void deleteFromIndexByUserIdInvalidUserId() {
//		setExpectedException("User id is Invalid - null");
//		transactionsSearchService.deleteFromIndexByUserId(null);
//	}
//
//	@Test
//	public void searchWrongParams() {
//		assertEquals(transactionsSearchService.searchTransactions( 1, "tes").isEmpty(), true);
//		assertEquals(transactionsSearchService.searchTransactions(TEST_CATEGORY_ID, "^").isEmpty(), true);
//	}
//
//	private SearchTransactions prepareSearchTransactions() {
//		SearchTransactions searchTransactions  = new SearchTransactions();
//		searchTransactions.getExpensesTransactions()
//				.add(new ExpensesTransaction(1, "123", "EUR", new Timestamp(System.currentTimeMillis()), new BigDecimal(100), "test description"));
//		searchTransactions.getExpensesTransactions()
//				.add(new ExpensesTransaction(2, "1223", "EUR", new Timestamp(System.currentTimeMillis()), new BigDecimal(200), "test description 2"));
//
////		searchTransactions.setUserId(TEST_USER_ID);
//		searchTransactions.setCategoryId(TEST_CATEGORY_ID);
//
//		return searchTransactions;
//	}
//
//	private void createTestIndex() {
//		addDelay(200);
//		transactionsSearchService.indexTransactions(prepareSearchTransactions());
//	}
//
//	private void addDelay(long mils) {
//		try {
//			Thread.sleep(mils);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//	}
//
//	private void setExpectedException(String expectedMessage) {
//		expectedException.expect(SearchServiceException.class);
//		expectedException.expectMessage(expectedMessage);
//	}
}
