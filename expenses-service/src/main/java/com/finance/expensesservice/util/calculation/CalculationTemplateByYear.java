package com.finance.expensesservice.util.calculation;

import java.util.Calendar;

public class CalculationTemplateByYear extends CalculationTemplate {

    @Override
    String getPeriodValue(Calendar calendar) {
        return String.valueOf(calendar.get(Calendar.YEAR));
    }
}
