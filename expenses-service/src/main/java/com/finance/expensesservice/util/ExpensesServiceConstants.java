package com.finance.expensesservice.util;

//TODO common library
public interface ExpensesServiceConstants {

    String DEFAULT_CATEGORY_NAME = "OTHER";

    interface MapFields {

       String PRINCIPAL = "principal";

       String ID = "id";
    }

    interface Order {

        String DEFAULT_ORDER = "asc";

        String ORDER_ASC = "asc";

        String ORDER_DESC = "desc";
    }

    interface DateFormat {
        String OUTPUT_DATE_FORMAT = "yyyy-MM-dd";

        String XLS_INPUT_DATE_FORMAT = "yyyyMMdd";
    }

    interface FileFormat {
        String PDF = "pdf";

        String XLS = "xls";
    }

    interface CalculationPeriod {
        String BY_MONTH = "byMonth";

        String BY_YEAR = "byYear";
    }
}
