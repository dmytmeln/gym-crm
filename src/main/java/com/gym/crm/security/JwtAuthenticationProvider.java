package com.gym.crm.security;

import com.gym.crm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        JwtTokenAuthentication jwtAuth = (JwtTokenAuthentication) authentication;

        String token = jwtAuth.getToken();
        jwtAuth.clearCredentials();

        if (!jwtService.isTokenValid(token)) {
            throw new BadCredentialsException("Invalid token");
        }

        String username = jwtService.extractUsername(token);
        Boolean active = userRepository.isActive(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        UserDetails userDetails = User.withUsername(username)
                .password("")
                .disabled(!active)
                .build();

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