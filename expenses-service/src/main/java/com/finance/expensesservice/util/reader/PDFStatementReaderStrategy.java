package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.domain.ExpensesTransaction;
import com.finance.expensesservice.util.reader.formatter.Formatter;

import java.io.InputStream;
import java.util.List;

public class PDFStatementReaderStrategy implements StatementReaderStrategy {

    private final Formatter formatter;

    PDFStatementReaderStrategy(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public List<ExpensesTransaction> read(InputStream input) {
        throw  new UnsupportedOperationException();
    }
}
