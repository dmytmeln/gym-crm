package com.gym.crm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;

import java.io.IOException;

import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.USER_DEACTIVATED_ERROR;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationEntryPointTest {

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private JwtAuthenticationEntryPoint entryPoint;

    @Test
    void shouldHandleInsufficientAuthenticationException() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(InsufficientAuthenticationException.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        verify(response).setHeader("WWW-Authenticate", "Bearer");
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertNotNull(actualError, "Error response should not be null");
        assertEquals(AUTHENTICATION_ERROR.getCode(), actualError.getErrorCode(), "Error code should match AUTHENTICATION_ERROR");
        assertEquals(AUTHENTICATION_ERROR.getMessage(), actualError.getErrorMessage(), "Error message should match AUTHENTICATION_ERROR");
    }

    @Test
    void shouldHandleDisabledException() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(DisabledException.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(403);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertNotNull(actualError, "Error response should not be null");
        assertEquals(USER_DEACTIVATED_ERROR.getCode(), actualError.getErrorCode(), "Error code should match USER_DEACTIVATED_ERROR");
        assertEquals(USER_DEACTIVATED_ERROR.getMessage(), actualError.getErrorMessage(), "Error message should match USER_DEACTIVATED_ERROR");
    }

    @Test
    void shouldHandleBadCredentialsException() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(BadCredentialsException.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertNotNull(actualError, "Error response should not be null");
        assertEquals(AUTHENTICATION_ERROR.getCode(), actualError.getErrorCode(), "Error code should match AUTHENTICATION_ERROR");
        assertEquals(AUTHENTICATION_ERROR.getMessage(), actualError.getErrorMessage(), "Error message should match AUTHENTICATION_ERROR");
    }

    @Test
    void shouldHandleUnexpectedAuthenticationException() throws IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AuthenticationException authException = mock(AuthenticationException.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        ArgumentCaptor<ErrorResponse> errorCaptor = ArgumentCaptor.forClass(ErrorResponse.class);

        when(response.getOutputStream()).thenReturn(outputStream);

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(401);
        verify(response).setContentType("application/json");
        verify(objectMapper).writeValue(eq(outputStream), errorCaptor.capture());
        ErrorResponse actualError = errorCaptor.getValue();
        assertNotNull(actualError, "Error response should not be null");
        assertEquals(AUTHENTICATION_ERROR.getCode(), actualError.getErrorCode(), "Error code should match AUTHENTICATION_ERROR");
        assertEquals(AUTHENTICATION_ERROR.getMessage(), actualError.getErrorMessage(), "Error message should match AUTHENTICATION_ERROR");
    }

}

