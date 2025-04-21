package com.jeanbarcellos.project112.serviceimperative.exception;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.jeanbarcellos.project112.core.constants.MessageConstants;
import com.jeanbarcellos.project112.core.dto.ErrorResponse;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // Jakarta ----------------------------------------------------------------

    /**
     * Relata o resultado de violações de restrições.(Jakarta)
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handle(ConstraintViolationException exception) {
        log.error("Constraint violation", exception);

        List<String> errors = this.createErrorMessages(exception.getConstraintViolations());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));
    }

    // Spring Web --------------------------------------------------------------

    /**
     * Exceção a ser lançada quando a validação de um argumento anotado com @Valid
     * falhar
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException exception) {
        log.error("Validation error", exception);

        List<String> errors = this.createErrorMessages(exception.getFieldErrors());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));

    }

    /**
     * Subclasse ServerWebInputException que indica uma falha de validação ou
     * vinculação de dados.
     */
    @ExceptionHandler(WebExchangeBindException.class)
    public ResponseEntity<ErrorResponse> handle(WebExchangeBindException exception) {
        log.error("WebExchange bind error", exception);

        List<String> errors = this.createErrorMessages(exception.getFieldErrors());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));

    }

    /**
     * Exceção para erros que correspondem ao status de resposta 400 (solicitação
     * inválida) para uso em aplicações Web Spring. A exceção fornece campos
     * adicionais (por exemplo, um MethodParameter opcional, se relacionado ao
     * erro).
     */
    @ExceptionHandler(ServerWebInputException.class)
    public ResponseEntity<ErrorResponse> handle(ServerWebInputException exception) {
        log.error("Server web input error", exception);

        List<String> errors = List.of(exception.getMostSpecificCause().getMessage());

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, errors));
    }

    /**
     * Lançado por implementações de HttpMessageConverter quando o método
     * HttpMessageConverter.read falha.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handle(HttpMessageNotReadableException exception) {
        log.error("Malformed JSON or type mismatch", exception);

        return ResponseEntity
                .badRequest()
                .body(ErrorResponse.of(MessageConstants.MGG_ERROR_VALIDATION_JSON_MALFORMATED));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handle(NoResourceFoundException exception) {
        log.error("No Resource Found", exception.getMessage());

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_NOT_FOUND));
    }

    @SuppressWarnings("null")
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException exception) {
        log.error("Method Argument Type Mismatch", exception.getMessage());

        var type = ObjectUtils.isEmpty(exception.getRequiredType()) ? String.class : exception.getRequiredType();
        var pattern = String.format("should be of type %s", type.getSimpleName());

        var error = String.format(MessageConstants.MSG_ERROR_VALIDATION_FIELD_LIST, exception.getName(), pattern);

        var response = ErrorResponse.of(MessageConstants.MSG_ERROR_VALIDATION, Arrays.asList(error));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST.value())
                .body(response);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handle(HttpRequestMethodNotSupportedException exception) {
        log.error("Http Request Method Not Supported", exception.getMessage());

        String error = String.format(MessageConstants.MSG_ERROR_HTTP_METHOD_NOT_SUPPORTED, exception.getMethod());

        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ErrorResponse.of(error));
    }

    // Hibernate --------------------------------------------------------------

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException exception) {
        log.warn("Entity not found", exception);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND.value())
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_ENTITY_NOT_FOUND));
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> handle(OptimisticLockingFailureException exception) {
        log.error("Optimistic Locking error", exception);

        return ResponseEntity
                .status(HttpStatus.CONFLICT.value())
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_OPTIMISTIC_LOCKING));
    }

    // Otthers Exceptions -----------------------------------------------------

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable exception) {
        log.error("Unexpected error", exception);

        return ResponseEntity
                .internalServerError()
                .body(ErrorResponse.of(MessageConstants.MSG_ERROR_SERVICE));
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