package com.finance.security.api.controller;

import com.finance.security.api.dto.ServiceError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(InvalidGrantException.class)
    public ResponseEntity<ServiceError> handleInvalidGrantException(InvalidGrantException e) {
        logger.error("message {} oauth2 code {}", e.getMessage(), e.getOAuth2ErrorCode());
        return new ResponseEntity<>(new ServiceError(e.getMessage(), e.getOAuth2ErrorCode()), HttpStatus.valueOf(e.getHttpErrorCode()));

    }



}
