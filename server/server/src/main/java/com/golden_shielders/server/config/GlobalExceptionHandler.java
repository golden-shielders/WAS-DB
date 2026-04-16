package com.golden_shielders.server.config;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(basePackages = "com.golden_shielders.server.controller")
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        Map<String, String> error = new HashMap<>();
        error.put("status", "500");
        error.put("exception", e.getClass().getName());
        error.put("message", e.getMessage());
        error.put("cause", e.getCause() != null ? e.getCause().toString() : "null");
        error.put("trace", Arrays.toString(e.getStackTrace()));
        return ResponseEntity.status(500).body(error);
    }
}