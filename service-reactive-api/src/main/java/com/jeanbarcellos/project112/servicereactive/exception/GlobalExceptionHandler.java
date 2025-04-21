package com.jeanbarcellos.project112.servicereactive.exception;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;

import com.jeanbarcellos.project112.core.constants.MessageConstants;
import com.jeanbarcellos.project112.core.dto.ErrorResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Jakarta ----------------------------------------------------------------

    @ExceptionHandler(ConstraintViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handle(ConstraintViolationException exception) {
        log.error("Constraint violation", exception);

        List<String> errors = this.createErrorMessages(exception.getConstraintViolations());

        return Mono.just(ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors)));
    }

    // Spring Web --------------------------------------------------------------

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handle(WebExchangeBindException exception) {
        log.error("WebExchange bind error", exception);

        List<String> errors = this.createErrorMessages(exception.getFieldErrors());

        return Mono.just(ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors)));

    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleServerWebInput(ServerWebInputException exception) {
        log.error("Server web input error", exception);

        List<String> errors = List.of(exception.getMostSpecificCause().getMessage());

        return Mono.just(ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors)));
    }

    @ExceptionHandler(Throwable.class)
    public Mono<ResponseEntity<ErrorResponse>> handle(Throwable exception) {
        log.error("Unexpected error", exception);

        return Mono.just(ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_SERVICE)));
    }

    // Helper methods ---------------------------------------------------------

    private List<String> createErrorMessages(List<FieldError> fieldErrors) {
        return fieldErrors
                .stream()
                .map(error -> String.format(
                        MessageConstants.MSG_ERROR_VALIDATION_FIELD_LIST,
                        error.getField(), error.getDefaultMessage()))
                .toList();
    }

    private List<String> createErrorMessages(Set<ConstraintViolation<?>> constraints) {
        return constraints
                .stream()
                .map(violation -> String.format(
                        MessageConstants.MSG_ERROR_VALIDATION_FIELD_LIST,
                        violation.getPropertyPath(), violation.getMessage()))
                .collect(Collectors.toList());
    }
}