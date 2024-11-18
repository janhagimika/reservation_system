package com.example.reservation_system.settings;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        String exceptionMessage = ex.getMessage();

        if (exceptionMessage.contains("Key (email)")) {
            errors.put("email", "Email je již registrován.");
        }
        if (exceptionMessage.contains("Key (username)")) {
            errors.put("username", "Uživatelské jméno je již obsazené.");
        }

        // Fallback for unknown issues
        if (errors.isEmpty()) {
            errors.put("error", "Došlo k chybě databáze. Zkuste to prosím znovu.");
        }

        return new ResponseEntity<>(errors, HttpStatus.CONFLICT);
    }
}
