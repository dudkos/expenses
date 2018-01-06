package com.finance.expensesservice.util;

import java.util.List;

public class Util {

    @SuppressWarnings("unchecked")
    public static <T> List<T> cast(List<? extends T> t) {
        return (List<T>) t;
    }
}
