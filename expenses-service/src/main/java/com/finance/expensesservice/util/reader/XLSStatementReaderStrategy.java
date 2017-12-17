package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.domain.Transaction;
import com.finance.expensesservice.util.reader.formatter.Formatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XLSStatementReaderStrategy implements StatementReaderStrategy {

    private final Formatter formatter;

    XLSStatementReaderStrategy(Formatter formatter) {
        this.formatter = formatter;
    }

    @Override
    public List<Transaction> read(InputStream input) throws Exception {
        Workbook workbook = WorkbookFactory.create(input);
        Sheet sheet = workbook.getSheetAt(0);
        List<Transaction> transactions = new ArrayList<>();
        sheet.forEach(row -> {
            if(!sheet.getRow(0).equals(row) && isRowValid(row)) transactions.add(convertToTransaction(row));
        });

        return transactions;
    }

    private Transaction convertToTransaction(Row row) {
        Transaction transaction = new Transaction();
        transaction.setAccountNumber(formatter.account(row.getCell(0).getNumericCellValue()));
        transaction.setAmount(formatter.amount(row.getCell(6).getNumericCellValue()));
        transaction.setCurrencyCode(row.getCell(1).getStringCellValue());
        transaction.setTransactionDate(formatter.date(row.getCell(3).getNumericCellValue()));
        transaction.setDescription(row.getCell(7).getStringCellValue());
        return transaction;
    }

    private boolean isRowValid(Row row) {
        return row.getCell(6) != null && row.getCell(3) != null && row.getCell(7) != null;
    }
}
