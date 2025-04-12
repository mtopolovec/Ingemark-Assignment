package com.ingemark.assignment.assignment.configuration;

import com.ingemark.assignment.assignment.dto.ApiResponseMsg;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FetchNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    // Custom fetch not found
    @ExceptionHandler(FetchNotFoundException.class)
    public ResponseEntity<ApiResponseMsg> handleNotFound(FetchNotFoundException ex) {
        log.warn("Not found: {}", ex.getMessage());
        ApiResponseMsg error = new ApiResponseMsg(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getEntityName(),
                List.of(ex.getMessage())
        );
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // Validation (e.g., @Valid on DTOs)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseMsg> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.toList());

        ApiResponseMsg error = new ApiResponseMsg(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields failed validation.",
                errors
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Catch constraint violations (e.g. @RequestParam validation)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseMsg> handleConstraintViolation(ConstraintViolationException ex) {
        List<String> violations = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                .collect(Collectors.toList());

        ApiResponseMsg error = new ApiResponseMsg(
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request Parameters",
                "Validation failed for request parameters.",
                violations
        );
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Catch-all fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseMsg> handleGeneralError(Exception ex) {
        log.error("Unhandled error", ex);
        ApiResponseMsg error = new ApiResponseMsg(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Something went wrong. Please try again later.",
                null
        );
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
