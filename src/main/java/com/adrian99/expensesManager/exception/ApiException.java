package com.adrian99.expensesManager.exception;

public class ApiException {

    private final String message;

    public ApiException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
