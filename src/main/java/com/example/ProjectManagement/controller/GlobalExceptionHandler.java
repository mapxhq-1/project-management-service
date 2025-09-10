package com.example.ProjectManagement.controller;

import com.example.ProjectManagement.Exception.InvalidProjectIdException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidProjectIdException.class)

    public ResponseEntity<String> handleInvalidProjectId(InvalidProjectIdException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
