package com.finance.expensesservice.controller;

import com.finance.common.dto.ServiceError;
import com.finance.common.exception.ServiceException;
import feign.FeignException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceError> handleExpensesServiceException(ServiceException e) {
        logger.error("message {} ", e.getMessage());
        return new ResponseEntity<>(new ServiceError(e.getMessage(), null), HttpStatus.valueOf(e.getStatus()));
    }

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ServiceError> handleFeignException(FeignException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(new ServiceError(e.status() == 503 ? "Search service unavailable."
                : e.getMessage(), null), HttpStatus.valueOf(e.status() == 0 ? 500 : e.status()));
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ServiceError> handleHttpClientErrorException(HttpClientErrorException e) {
        logger.error(e.getMessage());
        String message = e.getRawStatusCode() == 401 ?
                "Bad credentials" : e.getRawStatusCode() == 403 ? "You don't have permissions to use this resource." : e.getMessage();
        return new ResponseEntity<>(new ServiceError(message, null), e.getStatusCode());
    }
}
