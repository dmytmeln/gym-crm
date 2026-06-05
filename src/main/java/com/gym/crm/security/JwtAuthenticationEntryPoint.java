package com.gym.crm.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gia.openapi.model.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.gym.crm.exception.ApiError.AUTHENTICATION_ERROR;
import static com.gym.crm.exception.ApiError.USER_DEACTIVATED_ERROR;
import static org.springframework.http.HttpHeaders.WWW_AUTHENTICATE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setContentType(APPLICATION_JSON_VALUE);

        ErrorResponse errorResponse = switch (authException) {
            case InsufficientAuthenticationException ignored -> handleInvalidToken(response);
            case BadCredentialsException ignored -> handleInvalidToken(response);
            case UsernameNotFoundException ignored -> handleInvalidToken(response);
            case DisabledException ignored -> handleDeactivatedUser(response);
            default -> defaultHandler(response, authException);
        };

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }

    private ErrorResponse handleInvalidToken(HttpServletResponse response) {
        response.setStatus(AUTHENTICATION_ERROR.getStatus().value());
        response.setHeader(WWW_AUTHENTICATE, "Bearer");

        return new ErrorResponse(AUTHENTICATION_ERROR.getCode(), AUTHENTICATION_ERROR.getMessage());
    }

    private ErrorResponse handleDeactivatedUser(HttpServletResponse response) {
        response.setStatus(USER_DEACTIVATED_ERROR.getStatus().value());

        return new ErrorResponse(USER_DEACTIVATED_ERROR.getCode(), USER_DEACTIVATED_ERROR.getMessage());
    }

    private ErrorResponse defaultHandler(HttpServletResponse response, AuthenticationException authException) {
        log.error("Unexpected authentication exception: {}", authException.getMessage(), authException);
        response.setStatus(AUTHENTICATION_ERROR.getStatus().value());

        return new ErrorResponse(AUTHENTICATION_ERROR.getCode(), AUTHENTICATION_ERROR.getMessage());
    }

}
