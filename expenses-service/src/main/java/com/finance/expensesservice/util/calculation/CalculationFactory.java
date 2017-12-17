package com.finance.expensesservice.util.calculation;

import static com.finance.expensesservice.util.ExpensesServiceConstants.CalculationPeriod.BY_MONTH;
import static com.finance.expensesservice.util.ExpensesServiceConstants.CalculationPeriod.BY_YEAR;

public class CalculationFactory {

    public static CalculationTemplate calculationTemplate(String period) throws Exception {
        if(BY_MONTH.equals(period)) {
            return new CalculationTemplateByMonth();
        } else if(BY_YEAR.equals(period)){
            return new CalculationTemplateByYear();
        }

        throw new Exception("Unsupported calculation type");
    }
}
