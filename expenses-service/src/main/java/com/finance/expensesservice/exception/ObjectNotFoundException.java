package com.finance.expensesservice.exception;

import com.finance.common.exception.ServiceException;
import org.springframework.http.HttpStatus;

public class ObjectNotFoundException extends ServiceException {

    private static final Integer status = HttpStatus.NOT_FOUND.value();

    public ObjectNotFoundException(String message) {
        super(status, message);
    }
}
