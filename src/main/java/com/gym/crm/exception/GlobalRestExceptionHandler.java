package com.gym.crm.exception;

import com.gia.openapi.model.ErrorResponse;
import com.gym.crm.security.AuthenticationException;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalRestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        ApiError notFound = ApiError.NOT_FOUND;
        String message = "%s: %s".formatted(notFound.getMessage(), ex.getMessage());

        log.warn("Entity not found: {}", ex.getMessage());
        return buildResponse(notFound, message);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        ApiError validation = ApiError.VALIDATION;
        String message = "%s: %s".formatted(validation.getMessage(), ex.getMessage());

        log.warn("Validation error: {}", ex.getMessage());
        return buildResponse(validation, message);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return buildResponse(ApiError.AUTHENTICATION);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handleHibernateException(PersistenceException ex) {
        log.error("Database error occurred", ex);
        return buildResponse(ApiError.DATABASE);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(ApiError.SERVICE);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        String violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> "%s: %s".formatted(fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        ApiError validation = ApiError.VALIDATION;
        String message = "%s: %s".formatted(validation.getMessage(), violations);
        ErrorResponse body = new ErrorResponse(validation.getCode(), message);

        log.warn("Request body validation failed: {}", ex.getMessage());
        return ResponseEntity.status(validation.getStatus()).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(
            Exception ex,
            Object body,
            HttpHeaders headers,
            HttpStatusCode statusCode,
            WebRequest request) {
        ApiError apiError = getApiErrorForStatus(statusCode);
        ErrorResponse errorResponse = new ErrorResponse(apiError.getCode(), ex.getMessage());

        log.warn("Spring MVC exception occurred: {}", ex.getMessage());
        return ResponseEntity.status(statusCode).headers(headers).body(errorResponse);
    }

    private ApiError getApiErrorForStatus(HttpStatusCode status) {
        if (status.isSameCodeAs(HttpStatus.BAD_REQUEST)) {
            return ApiError.VALIDATION;
        } else if (status.isSameCodeAs(HttpStatus.UNAUTHORIZED)) {
            return ApiError.AUTHENTICATION;
        } else if (status.isSameCodeAs(HttpStatus.FORBIDDEN)) {
            return ApiError.AUTHORIZATION;
        } else if (status.isSameCodeAs(HttpStatus.NOT_FOUND)) {
            return ApiError.NOT_FOUND;
        } else {
            return ApiError.SERVICE;
        }
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiError apiError) {
        return buildResponse(apiError, apiError.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiError apiError, String message) {
        ErrorResponse errorResponse = new ErrorResponse(apiError.getCode(), message);
        return ResponseEntity.status(apiError.getStatus()).body(errorResponse);
    }

}
