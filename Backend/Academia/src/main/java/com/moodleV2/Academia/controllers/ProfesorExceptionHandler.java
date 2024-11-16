package com.moodleV2.Academia.controllers;

import com.moodleV2.Academia.exceptions.ProfesorNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Map;

@ControllerAdvice
public class ProfesorExceptionHandler {
    @ExceptionHandler(ProfesorNotFoundException.class)
    public ResponseEntity<?> handleProfesorNotFoundException(ProfesorNotFoundException ex, HttpServletRequest request) {
        Map<String, Object> body = Map.of(
                "timestamp", LocalDateTime.now(),
                "status", HttpStatus.NOT_FOUND.value(),
                "error", "Profesor Not Found",
                "message", ex.getMessage(),
                "path", request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
