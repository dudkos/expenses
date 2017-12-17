package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.domain.Transaction;

import java.io.InputStream;
import java.util.List;

public class ReaderContext {

    private StatementReaderStrategy statementReaderStrategy;

    public ReaderContext(StatementReaderStrategy statementReaderStrategy) {
        this.statementReaderStrategy = statementReaderStrategy;
    }

    public List<Transaction> readTransactionsFromFile(InputStream input) throws Exception {
        if(this.statementReaderStrategy == null) throw new Exception("Statement reader strategy is null");
        return this.statementReaderStrategy.read(input);
    }
}
