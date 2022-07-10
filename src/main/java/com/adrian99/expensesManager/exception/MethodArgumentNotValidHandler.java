package com.adrian99.expensesManager.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class MethodArgumentNotValidHandler {

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    public ResponseEntity<Object> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = "message";
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<Object> handlerConstraintException(ConstraintViolationException ex){
        Map<String, String> errors = new HashMap<>();

        ex.getConstraintViolations().forEach((error) -> {
            String fieldName = "message";
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });

        return new ResponseEntity<>(errors,HttpStatus.BAD_REQUEST);
    }
}
