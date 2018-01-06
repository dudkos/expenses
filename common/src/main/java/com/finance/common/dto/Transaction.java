package com.finance.common.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public interface Transaction {

    Integer getId();

    String getCurrencyCode();

    Timestamp getTransactionDate();

    BigDecimal getAmount();

    String getDescription();
}
