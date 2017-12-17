package com.finance.expensesservice.exception;

public class ExpensesServiceException extends RuntimeException {

    private Integer status;

    private String message;

    public ExpensesServiceException(Integer status, String message) {
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
