package com.finanse.search.api.controller;

import com.finance.common.dto.ServiceError;
import com.finance.common.exception.ServiceException;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerController {

    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ServiceError> handleServiceException(ServiceException e) {
        logger.error("message {} ", e.getMessage());
        return new ResponseEntity<>(new ServiceError(e.getMessage(), null), HttpStatus.valueOf(e.getStatus()));
    }

    @ExceptionHandler(NoNodeAvailableException.class)
    public ResponseEntity<ServiceError> handleNoNodeAvailableException(NoNodeAvailableException e) {
        logger.error("message {} ", e.getMessage());
        return new ResponseEntity<>(new ServiceError("Search service is not available. Please try later.", null), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
