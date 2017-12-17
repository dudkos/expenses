package com.finance.expensesservice.controller;

import com.finance.expensesservice.dto.ServiceError;
import com.finance.expensesservice.exception.ExpensesServiceException;
import com.sun.prism.impl.FactoryResetException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(ExpensesServiceException.class)
    public ResponseEntity<ServiceError> handleExpensesServiceException(ExpensesServiceException e) {
        logger.error("message {} ", e.getMessage());
        return new ResponseEntity<>(new ServiceError(e.getMessage(), null), HttpStatus.valueOf(e.getStatus()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ServiceError> handleFeignException(FeignException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new ServiceError(e.status() == 503 ? "Search service unavailable"
                : e.getMessage(), null), HttpStatus.valueOf(e.status()));
    }
}
