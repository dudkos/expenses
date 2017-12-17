package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.domain.Transaction;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface StatementReaderStrategy {

    List<Transaction> read(InputStream input) throws Exception;
}
