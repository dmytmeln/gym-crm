package com.gym.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final AuthenticationManager authenticationManager;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Optional<String> jwtToken = getJwtTokenFromAuthHeader(request);
        if (jwtToken.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            authenticateUser(jwtToken.get());
        } catch (AuthenticationException e) {
            handleAuthenticationFailure(request, response, e);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String token) throws AuthenticationException {
        Authentication authenticated = authenticateToken(token);

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authenticated);
        SecurityContextHolder.setContext(context);
    }

    private Authentication authenticateToken(String token) {
        JwtTokenAuthentication unauthenticated = JwtTokenAuthentication.unauthenticated(token);
        return authenticationManager.authenticate(unauthenticated);
    }

    private void handleAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException {
        SecurityContextHolder.clearContext();
        jwtAuthenticationEntryPoint.commence(request, response, e);
    }

    private Optional<String> getJwtTokenFromAuthHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(this::isBearerAuthHeader)
                .map(this::extractBearerToken);
    }

    private boolean isBearerAuthHeader(String header) {
        return header.startsWith(BEARER_PREFIX);
    }

    private String extractBearerToken(String header) {
        return header.substring(BEARER_PREFIX.length());
    }

}