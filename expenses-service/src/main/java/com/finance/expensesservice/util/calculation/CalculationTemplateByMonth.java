package com.finance.expensesservice.util.calculation;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class CalculationTemplateByMonth extends CalculationTemplate {

    @Override
    String getPeriodValue(Calendar calendar) {
        return getMonthFromInt(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR);
    }

    private static String getMonthFromInt(int num) {
        String month = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
}
