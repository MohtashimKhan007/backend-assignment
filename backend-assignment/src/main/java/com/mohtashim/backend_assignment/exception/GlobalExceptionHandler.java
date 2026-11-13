package com.mohtashim.backend_assignment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", ex.getStatus().value());
        error.put("error", ex.getStatus().getReasonPhrase());
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, ex.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_REQUEST.value());
        error.put("error", "Bad Request");

        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fe.getField(), fe.getDefaultMessage());
        }
        error.put("validationErrors", fieldErrors);
        error.put("message", "Validation failed for request body.");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<Map<String, Object>> handleHttpClientErrorException(HttpClientErrorException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());

        if (ex.getStatusCode().value() == 403 || ex.getStatusCode().value() == 429) {
            error.put("status", HttpStatus.TOO_MANY_REQUESTS.value());
            error.put("error", "Too Many Requests");
            error.put("message", "GitHub API rate limit exceeded. Please wait a moment or configure a GITHUB_TOKEN.");
            return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
        } else if (ex.getStatusCode().value() == 404) {
            error.put("status", HttpStatus.NOT_FOUND.value());
            error.put("error", "Not Found");
            error.put("message", "Resource not found on GitHub.");
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        error.put("status", ex.getStatusCode().value());
        error.put("error", "GitHub API Error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, ex.getStatusCode());
    }

    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<Map<String, Object>> handleRestClientException(RestClientException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.BAD_GATEWAY.value());
        error.put("error", "Bad Gateway");
        error.put("message", "Failed to communicate with GitHub API: " + ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("error", "Not Found");
        error.put("message", "Endpoint or resource not found. Check Swagger docs at /swagger-ui.html for available routes.");
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGlobalException(Exception ex) {
        Map<String, Object> error = new HashMap<>();
        error.put("timestamp", LocalDateTime.now());
        error.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.put("error", "Internal Server Error");
        error.put("message", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}