package com.gym.crm.exception;

import com.gia.openapi.model.ErrorResponse;
import com.gym.crm.security.AuthenticationException;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.request.WebRequest;

import java.util.List;

import static com.gym.crm.entity.EntityType.USER;
import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.AUTHORIZATION_ERROR;
import static com.gym.crm.exception.ApiError.DATABASE_ERROR;
import static com.gym.crm.exception.ApiError.NOT_FOUND_ERROR;
import static com.gym.crm.exception.ApiError.SERVICE_ERROR;
import static com.gym.crm.exception.ApiError.VALIDATION_ERROR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

class GlobalExceptionHandlerTest {

    private static final String EXPECTED_ERROR_MESSAGE_TEMPLATE = "%s: %s";

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void shouldHandleExceptionInternalWithBadRequestStatus() {
        Exception exception = new Exception("Bad request message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(exception, null, new HttpHeaders(), BAD_REQUEST, mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Bad request message");
    }

    @Test
    void shouldHandleExceptionInternalWithUnauthorizedStatus() {
        Exception exception = new Exception("Unauthorized message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(exception, null, new HttpHeaders(), UNAUTHORIZED, mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Unauthorized message");
    }

    @Test
    void shouldHandleExceptionInternalWithForbiddenStatus() {
        Exception exception = new Exception("Forbidden message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(exception, null, new HttpHeaders(), FORBIDDEN, mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(AUTHORIZATION_ERROR.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Forbidden message");
    }

    @Test
    void shouldHandleExceptionInternalWithNotFoundStatus() {
        Exception exception = new Exception("Not found message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(exception, null, new HttpHeaders(), NOT_FOUND, mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(NOT_FOUND_ERROR.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Not found message");
    }

    @Test
    void shouldHandleExceptionInternalWithOtherStatus() {
        Exception exception = new Exception("Internal server error message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(exception, null, new HttpHeaders(), INTERNAL_SERVER_ERROR, mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(SERVICE_ERROR.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Internal server error message");
    }

    @Test
    void shouldHandleEntityNotFoundException() {
        EntityNotFoundException exception = EntityNotFoundException.forUsername(USER, "username");

        ResponseEntity<ErrorResponse> response = handler.handleEntityNotFoundException(exception);

        assertThat(response.getStatusCode()).isEqualTo(NOT_FOUND_ERROR.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(NOT_FOUND_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(buildExpectedErrorMessage(NOT_FOUND_ERROR, exception));
    }

    @Test
    void shouldHandleValidationException() {
        ValidationException exception = new ValidationException("Test validation message");

        ResponseEntity<ErrorResponse> response = handler.handleValidationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(VALIDATION_ERROR.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(buildExpectedErrorMessage(VALIDATION_ERROR, exception));
    }

    @Test
    void shouldHandleAuthenticationException() {
        AuthenticationException exception = new AuthenticationException("Test auth message");

        ResponseEntity<ErrorResponse> response = handler.handleAuthenticationException(exception);

        assertThat(response.getStatusCode()).isEqualTo(AUTHENTICATION_ERROR.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(AUTHENTICATION_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(AUTHENTICATION_ERROR.getMessage());
    }

    @Test
    void shouldHandleHibernateException() {
        PersistenceException exception = new PersistenceException("Test database message");

        ResponseEntity<ErrorResponse> response = handler.handleHibernateException(exception);

        assertThat(response.getStatusCode()).isEqualTo(DATABASE_ERROR.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(DATABASE_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(DATABASE_ERROR.getMessage());
    }

    @Test
    void shouldHandleUnexpectedException() {
        Exception exception = new Exception("Test unexpected message");

        ResponseEntity<ErrorResponse> response = handler.handleUnexpectedException(exception);

        assertThat(response.getStatusCode()).isEqualTo(SERVICE_ERROR.getStatus());
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getErrorCode()).isEqualTo(SERVICE_ERROR.getCode());
        assertThat(response.getBody().getErrorMessage()).isEqualTo(SERVICE_ERROR.getMessage());
    }

    @Test
    void shouldHandleMethodArgumentNotValid() {
        MethodArgumentNotValidException exception = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        FieldError fieldError = new FieldError("objectName", "fieldName", "defaultMessage");

        when(exception.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<Object> response = handler.handleMethodArgumentNotValid(exception, new HttpHeaders(), BAD_REQUEST, mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(VALIDATION_ERROR.getStatus());
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(VALIDATION_ERROR.getCode());
        assertThat(body.getErrorMessage()).isEqualTo(String.format("%s: defaultMessage: fieldName", VALIDATION_ERROR.getMessage()));
    }

    private String buildExpectedErrorMessage(ApiError apiError, Exception exception) {
        return String.format(EXPECTED_ERROR_MESSAGE_TEMPLATE, apiError.getMessage(), exception.getMessage());
    }

}
