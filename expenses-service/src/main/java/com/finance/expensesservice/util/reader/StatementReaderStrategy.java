package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.domain.ExpensesTransaction;

import java.io.InputStream;
import java.util.List;

public interface StatementReaderStrategy {

    List<ExpensesTransaction> read(InputStream input) throws Exception;
}
