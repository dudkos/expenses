package com.finance.expensesservice.util.reader.formatter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.finance.expensesservice.util.ExpensesServiceConstants.DateFormat.XLS_INPUT_DATE_FORMAT;

public class FormatterImpl implements Formatter {

    private static final Formatter instance = new FormatterImpl();

    public static Formatter instance() {
        return instance;
    }

    @Override
    public Timestamp date(double date) {
        SimpleDateFormat sdf = new SimpleDateFormat(XLS_INPUT_DATE_FORMAT);
        try {
            return new Timestamp(sdf.parse(new BigDecimal(date).toPlainString()).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public BigDecimal amount(double amount) {
        return new BigDecimal(amount).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public String account(double account) {
        return new BigDecimal(account).toPlainString();
    }
}
