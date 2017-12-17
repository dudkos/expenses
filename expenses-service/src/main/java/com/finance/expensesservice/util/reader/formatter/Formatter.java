package com.finance.expensesservice.util.reader.formatter;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface Formatter {

    Timestamp date(double date);

    BigDecimal amount(double amount);

    String account(double account);
}
