package com.finance.expensesservice.util.calculation;

import java.text.DateFormatSymbols;
import java.util.Calendar;

import static com.finance.expensesservice.util.ExpensesServiceConstants.CalculationPeriod.BY_ALL;
import static com.finance.expensesservice.util.ExpensesServiceConstants.CalculationPeriod.BY_MONTH;
import static com.finance.expensesservice.util.ExpensesServiceConstants.CalculationPeriod.BY_YEAR;

public class CalculationFactory {

    public CalculationTemplate calculationTemplate(String period) {
        if(BY_MONTH.equals(period)) {
            return new CalculationTemplate() {
                @Override
                String getPeriodValue(Calendar calendar) {
                    return getMonthFromInt(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR);
                }
            };
        } else if(BY_YEAR.equals(period)){
            return new CalculationTemplate() {
                @Override
                String getPeriodValue(Calendar calendar) {
                    return String.valueOf(calendar.get(Calendar.YEAR));
                }
            };
        } else if(BY_ALL.equals(period)){
            return new CalculationTemplate() {
                @Override //TODO year period
                String getPeriodValue(Calendar calendar) {
                    return "ALL";
                }
            };
        }

        throw new UnsupportedOperationException("Unsupported calculation type");
    }

    private String getMonthFromInt(int num) {
        String month = null;
        DateFormatSymbols dfs = new DateFormatSymbols();
        String[] months = dfs.getMonths();
        if (num >= 0 && num <= 11 ) {
            month = months[num];
        }
        return month;
    }
}
