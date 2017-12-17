package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.util.reader.formatter.Formatter;
import com.finance.expensesservice.util.reader.formatter.FormatterImpl;

import java.util.HashMap;
import java.util.Map;

import static com.finance.expensesservice.util.ExpensesServiceConstants.FileFormat.PDF;
import static com.finance.expensesservice.util.ExpensesServiceConstants.FileFormat.XLS;

public class StatementReaderFactory {

    public static StatementReaderStrategy readerStrategy(String fileName) {
        if(fileName != null) {
            if(fileName.endsWith(PDF)) {
                return new PDFStatementReaderStrategy(FormatterImpl.instance());
            } else if(fileName.endsWith(XLS)) {
                return new XLSStatementReaderStrategy(FormatterImpl.instance());
            }
        }

        return null;
    }
}
