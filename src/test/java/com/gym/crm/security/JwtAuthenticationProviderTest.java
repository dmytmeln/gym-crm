package com.gym.crm.security;

import com.gym.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationProviderTest {

    private static final String USERNAME = "liam.miller";
    private static final String TOKEN = "validToken";

    @Mock
    private JwtService jwtService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private JwtAuthenticationProvider tokenProvider;

    @Test
    void shouldAuthenticateSuccessfullyWhenTokenIsValid() {
        JwtTokenAuthentication authentication = JwtTokenAuthentication.unauthenticated(TOKEN);

        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userRepository.isActive(USERNAME)).thenReturn(Optional.of(true));

        Authentication result = tokenProvider.authenticate(authentication);

        assertNotNull(result);
        assertTrue(result.isAuthenticated());
        UserDetails principal = (UserDetails) result.getPrincipal();
        assertEquals(USERNAME, principal.getUsername());
        assertTrue(principal.isEnabled());
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenTokenIsInvalid() {
        JwtTokenAuthentication authentication = JwtTokenAuthentication.unauthenticated(TOKEN);

        when(jwtService.isTokenValid(TOKEN)).thenReturn(false);

        assertThrows(BadCredentialsException.class, () -> tokenProvider.authenticate(authentication));
    }

    @Test
    void shouldThrowDisabledExceptionWhenUserIsDeactivated() {
        JwtTokenAuthentication authentication = JwtTokenAuthentication.unauthenticated(TOKEN);

        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userRepository.isActive(USERNAME)).thenReturn(Optional.of(false));

        assertThrows(DisabledException.class, () -> tokenProvider.authenticate(authentication));
    }

    @Test
    void shouldThrowBadCredentialsExceptionWhenUserNotFound() {
        JwtTokenAuthentication authentication = JwtTokenAuthentication.unauthenticated(TOKEN);

        when(jwtService.isTokenValid(TOKEN)).thenReturn(true);
        when(jwtService.extractUsername(TOKEN)).thenReturn(USERNAME);
        when(userRepository.isActive(USERNAME)).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> tokenProvider.authenticate(authentication));
    }

    @Test
    void shouldSupportJwtTokenAuthentication() {
        boolean supportsJwt = tokenProvider.supports(JwtTokenAuthentication.class);
        boolean supportsOther = tokenProvider.supports(UsernamePasswordAuthenticationToken.class);

        assertTrue(supportsJwt);
        assertFalse(supportsOther);
    }

}
