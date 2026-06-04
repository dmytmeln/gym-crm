package com.gym.crm.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtTokenAuthentication jwtAuth = (JwtTokenAuthentication) authentication;

        String token = jwtAuth.getToken();
        jwtAuth.clearCredentials();

        if (!jwtService.isTokenValid(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        String username = jwtService.extractUsername(token);
        UserDetails userDetails = userDetailsService.loadSimpleUserByUsername(username);

        if (!userDetails.isEnabled()) {
            throw new DisabledException("User account is deactivated. Please contact support.");
        }

        return JwtTokenAuthentication.authenticated(userDetails);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return JwtTokenAuthentication.class.isAssignableFrom(authentication);
    }

}