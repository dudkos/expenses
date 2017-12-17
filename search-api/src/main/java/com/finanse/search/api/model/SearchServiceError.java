package com.finanse.search.api.model;

public class SearchServiceError {

    private int code;
    private String message;

    public SearchServiceError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
