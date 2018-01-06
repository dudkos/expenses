package com.finance.common.exception;

public class ServiceException extends RuntimeException {

    private Integer status;

    private String message;

    public ServiceException(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
