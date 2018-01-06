package com.finance.expensesservice.util.reader;

import com.finance.expensesservice.domain.ExpensesTransaction;
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
    public List<ExpensesTransaction> read(InputStream input) throws Exception {
        Workbook workbook = WorkbookFactory.create(input);
        Sheet sheet = workbook.getSheetAt(0);
        List<ExpensesTransaction> expensesTransactions = new ArrayList<>();
        sheet.forEach(row -> {
            if(!sheet.getRow(0).equals(row) && isRowValid(row)) expensesTransactions.add(convertToTransaction(row));
        });

        return expensesTransactions;
    }

    private ExpensesTransaction convertToTransaction(Row row) {
        ExpensesTransaction expensesTransaction = new ExpensesTransaction();
        expensesTransaction.setAccountNumber(formatter.account(row.getCell(0).getNumericCellValue()));
        expensesTransaction.setAmount(formatter.amount(row.getCell(6).getNumericCellValue()));
        expensesTransaction.setCurrencyCode(row.getCell(1).getStringCellValue());
        expensesTransaction.setTransactionDate(formatter.date(row.getCell(3).getNumericCellValue()));
        expensesTransaction.setDescription(row.getCell(7).getStringCellValue());
        return expensesTransaction;
    }

    private boolean isRowValid(Row row) {
        return row.getCell(6) != null && row.getCell(3) != null && row.getCell(7) != null;
    }
}
