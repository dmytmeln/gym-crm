package com.gym.crm.exception;

import com.gia.openapi.model.ErrorResponse;
import jakarta.persistence.PersistenceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.AUTHORIZATION_ERROR;
import static com.gym.crm.exception.ApiError.CONFLICT_ERROR;
import static com.gym.crm.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.exception.ApiError.USER_DEACTIVATED_ERROR;
import static com.gym.crm.exception.ApiError.VALIDATION_ERROR;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String RESPONSE_MESSAGE_TEMPLATE = "%s: %s";

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        String message = String.format(RESPONSE_MESSAGE_TEMPLATE, NOT_FOUND_ERROR.getMessage(), ex.getMessage());

        log.warn("Entity not found: {}", ex.getMessage());
        return buildResponse(NOT_FOUND_ERROR, message);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(ValidationException ex) {
        String message = String.format(RESPONSE_MESSAGE_TEMPLATE, VALIDATION_ERROR.getMessage(), ex.getMessage());

        log.warn("Validation error: {}", ex.getMessage());
        return buildResponse(VALIDATION_ERROR, message);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(ConflictException ex) {
        String message = String.format(RESPONSE_MESSAGE_TEMPLATE, CONFLICT_ERROR.getMessage(), ex.getMessage());

        log.warn("Conflict error: {}", ex.getMessage());
        return buildResponse(CONFLICT_ERROR, message);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
        log.warn("Authentication error: {}", ex.getMessage());
        return buildResponse(AUTHENTICATION_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return buildResponse(AUTHORIZATION_ERROR);
    }

    @ExceptionHandler(UserDeactivatedException.class)
    public ResponseEntity<ErrorResponse> handleUserDeactivatedException(UserDeactivatedException ex) {
        log.warn("Deactivated user login attempt: {}", ex.getMessage());
        return buildResponse(USER_DEACTIVATED_ERROR);
    }

    @ExceptionHandler(PersistenceException.class)
    public ResponseEntity<ErrorResponse> handleHibernateException(PersistenceException ex) {
        log.error("Database error occurred", ex);
        return buildResponse(DATABASE_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildResponse(SERVICE_ERROR);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@NonNull MethodArgumentNotValidException ex,
                                                                  @NonNull HttpHeaders headers,
                                                                  @NonNull HttpStatusCode status,
                                                                  @NonNull WebRequest request) {
        String violations = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> String.format(RESPONSE_MESSAGE_TEMPLATE, fieldError.getField(), fieldError.getDefaultMessage()))
                .collect(Collectors.joining(", "));

        String message = String.format(RESPONSE_MESSAGE_TEMPLATE, VALIDATION_ERROR.getMessage(), violations);
        ErrorResponse body = new ErrorResponse(VALIDATION_ERROR.getCode(), message);

        log.warn("Request body validation failed: {}", ex.getMessage());
        return ResponseEntity.status(VALIDATION_ERROR.getStatus()).body(body);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(@NonNull Exception ex,
                                                             @Nullable Object body,
                                                             @NonNull HttpHeaders headers,
                                                             @NonNull HttpStatusCode statusCode,
                                                             @NonNull WebRequest request) {
        ApiError apiError = getApiErrorForStatus(statusCode);
        ErrorResponse errorResponse = new ErrorResponse(apiError.getCode(), ex.getMessage());

        log.warn("Spring MVC exception occurred: {}", ex.getMessage());
        return ResponseEntity.status(statusCode).headers(headers).body(errorResponse);
    }

    private ApiError getApiErrorForStatus(HttpStatusCode status) {
        if (!(status instanceof HttpStatus httpStatus)) {
            return SERVICE_ERROR;
        }

        return switch (httpStatus) {
            case BAD_REQUEST -> VALIDATION_ERROR;
            case UNAUTHORIZED -> AUTHENTICATION_ERROR;
            case FORBIDDEN -> AUTHORIZATION_ERROR;
            case NOT_FOUND -> NOT_FOUND_ERROR;
            case CONFLICT -> CONFLICT_ERROR;
            default -> SERVICE_ERROR;
        };
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiError apiError) {
        return buildResponse(apiError, apiError.getMessage());
    }

    private ResponseEntity<ErrorResponse> buildResponse(ApiError apiError, String message) {
        ErrorResponse errorResponse = new ErrorResponse(apiError.getCode(), message);
        return ResponseEntity.status(apiError.getStatus()).body(errorResponse);
    }

}
