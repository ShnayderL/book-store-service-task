package com.epam.rd.autocode.spring.project.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(NotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {} | Request: {} | Message: {}",
                ex.getClass().getSimpleName(),
                request.getDescription(false),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(AlreadyExistException.class)
    public ResponseEntity<String> handleAlreadyExistException(AlreadyExistException ex, WebRequest request) {
        log.warn("Conflict detected: {} | Request: {} | Message: {}",
                ex.getClass().getSimpleName(),
                request.getDescription(false),
                ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex, WebRequest request) {
        log.error("Unexpected error occurred: {} | Request: {} | Message: {} | StackTrace: {}",
                ex.getClass().getName(),
                request.getDescription(false),
                ex.getMessage(),
                ex.getStackTrace());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Internal server error: " + ex.getMessage());
    }
}