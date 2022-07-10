package com.adrian99.expensesManager.exception;

import org.springframework.http.HttpStatus;

public class ApiRequestException extends RuntimeException{

    private HttpStatus httpStatus;

    public ApiRequestException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

    public ApiRequestException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public ApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
