package com.finance.expensesservice.exception;

import org.springframework.http.HttpStatus;

public class ObjectNotFoundException extends ExpensesServiceException {

    private static final Integer status = HttpStatus.NOT_FOUND.value();

    public ObjectNotFoundException(String message) {
        super(status, message);
    }
}
