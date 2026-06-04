package com.gym.crm.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
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

    private final AuthenticationManager authenticationManager;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        getJwtTokenFromAuthHeader(request).ifPresent(this::authenticateUser);

        filterChain.doFilter(request, response);
    }

    private void authenticateUser(String jwtToken) {
        JwtTokenAuthentication auth = JwtTokenAuthentication.unauthenticated(jwtToken);
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        Authentication tokenAuthentication = authenticationManager.authenticate(auth);
        context.setAuthentication(tokenAuthentication);

        SecurityContextHolder.setContext(context);
    }

    private Optional<String> getJwtTokenFromAuthHeader(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .filter(this::isBearerAuthHeader)
                .map(this::extractBearerToken);
    }

    private boolean isBearerAuthHeader(String header) {
        return header.startsWith("Bearer ");
    }

    private String extractBearerToken(String header) {
        return header.substring(7);
    }

}