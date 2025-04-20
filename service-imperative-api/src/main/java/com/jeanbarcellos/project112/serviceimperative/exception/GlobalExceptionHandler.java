package com.jeanbarcellos.project112.serviceimperative.exception;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import com.jeanbarcellos.core.constants.MessageConstants;
import com.jeanbarcellos.core.dto.ErrorResponse;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());

        List<String> errors = exception.getFieldErrors()
                .stream()
                .map(e -> String.format(
                        MessageConstants.MSG_ERROR_VALIDATION_FIELD_LIST,
                        e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));

    }

    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handle(WebExchangeBindException exception) {
        log.error(exception.getMessage());

        List<String> errors = exception.getFieldErrors()
                .stream()
                .map(e -> String.format(
                        MessageConstants.MSG_ERROR_VALIDATION_FIELD_LIST,
                        e.getField(), e.getDefaultMessage()))
                .toList();

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));

    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException exception) {
        log.error(exception.getMessage());

        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(violation -> String.format(
                        MessageConstants.MSG_ERROR_VALIDATION_FIELD_LIST,
                        violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.toList());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));
    }

    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handleServerWebInput(ServerWebInputException exception) {
        log.error(exception.getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(
                        MessageConstants.MSG_ERROR_VALIDATION,
                        List.of(exception.getMostSpecificCause().getMessage())));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable exception) {
        log.error(exception.getMessage(), exception);

        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_SERVICE));
    }

}