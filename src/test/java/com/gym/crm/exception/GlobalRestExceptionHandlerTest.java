package com.gym.crm.exception;

import com.gia.openapi.model.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class GlobalRestExceptionHandlerTest {

    private final GlobalRestExceptionHandler handler = new GlobalRestExceptionHandler();

    @Test
    void shouldHandleExceptionInternalWithBadRequestStatus() {
        Exception exception = new Exception("Bad request message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                exception,
                null,
                new HttpHeaders(),
                HttpStatus.BAD_REQUEST,
                mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(ApiError.VALIDATION.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Bad request message");
    }

    @Test
    void shouldHandleExceptionInternalWithUnauthorizedStatus() {
        Exception exception = new Exception("Unauthorized message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                exception,
                null,
                new HttpHeaders(),
                HttpStatus.UNAUTHORIZED,
                mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(ApiError.AUTHENTICATION.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Unauthorized message");
    }

    @Test
    void shouldHandleExceptionInternalWithForbiddenStatus() {
        Exception exception = new Exception("Forbidden message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                exception,
                null,
                new HttpHeaders(),
                HttpStatus.FORBIDDEN,
                mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(ApiError.AUTHORIZATION.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Forbidden message");
    }

    @Test
    void shouldHandleExceptionInternalWithNotFoundStatus() {
        Exception exception = new Exception("Not found message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                exception,
                null,
                new HttpHeaders(),
                HttpStatus.NOT_FOUND,
                mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(ApiError.NOT_FOUND.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Not found message");
    }

    @Test
    void shouldHandleExceptionInternalWithOtherStatus() {
        Exception exception = new Exception("Internal server error message");

        ResponseEntity<Object> response = handler.handleExceptionInternal(
                exception,
                null,
                new HttpHeaders(),
                HttpStatus.INTERNAL_SERVER_ERROR,
                mock(WebRequest.class));

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isInstanceOf(ErrorResponse.class);
        ErrorResponse body = (ErrorResponse) response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getErrorCode()).isEqualTo(ApiError.SERVICE.getCode());
        assertThat(body.getErrorMessage()).isEqualTo("Internal server error message");
    }

}
