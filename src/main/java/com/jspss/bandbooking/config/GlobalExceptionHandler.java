package com.jspss.bandbooking.config;

import com.jspss.bandbooking.exceptions.BadRequestException;
import com.jspss.bandbooking.exceptions.ConflictException;
import com.jspss.bandbooking.exceptions.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import org.slf4j.MDC;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;


@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiErrorResponse> handleRuntime(RuntimeException ex){

        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse(UUID.randomUUID().toString());

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                "RuntimeException",
                HttpStatus.BAD_REQUEST.value(),
                "RUNTIME_ERROR",
                ZonedDateTime.now(),
                traceId
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException ex){

        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse(UUID.randomUUID().toString());

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err-> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");

        ApiErrorResponse response = new ApiErrorResponse(
                message,
                "Validation Error",
                HttpStatus.BAD_REQUEST.value(),
                "VALIDATION_ERROR",
                ZonedDateTime.now(),
                traceId
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(ConstraintViolationException ex){

        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse(UUID.randomUUID().toString());

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                "Constraint Violation",
                HttpStatus.BAD_REQUEST.value(),
                "CONSTRAINT_VIOLATION",
                ZonedDateTime.now(),
                traceId
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiErrorResponse> handleNotFound(NotFoundException ex) {

        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse(UUID.randomUUID().toString());

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                "NotFoundException",
                HttpStatus.NOT_FOUND.value(),
                "NOT_FOUND",
                ZonedDateTime.now(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiErrorResponse> handleBadRequest (BadRequestException ex){

        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse(UUID.randomUUID().toString());

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                "BadRequestException",
                HttpStatus.BAD_REQUEST.value(),
                "BAD_REQUEST",
                ZonedDateTime.now(),
                traceId
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiErrorResponse> handleConflict(ConflictException ex) {

        String traceId = Optional.ofNullable(MDC.get("traceId"))
                .orElse(UUID.randomUUID().toString());

        ApiErrorResponse response = new ApiErrorResponse(
                ex.getMessage(),
                "ConflictException",
                HttpStatus.CONFLICT.value(),
                "BOOKING_CONFLICT",
                ZonedDateTime.now(),
                traceId
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }
}
