package com.gym.crm.security;

import com.gym.crm.entity.User;
import com.gym.crm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    private static final String USERNAME = "liam.miller";
    private static final String PASSWORD = "hashedPassword";

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {
        User user = buildUser(true);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
        assertTrue(result.isEnabled());
    }

    @Test
    void shouldLoadUserByUsernameAsDisabledWhenUserIsDeactivated() {
        User user = buildUser(false);

        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.of(user));

        UserDetails result = service.loadUserByUsername(USERNAME);

        assertNotNull(result);
        assertEquals(USERNAME, result.getUsername());
        assertEquals(PASSWORD, result.getPassword());
        assertFalse(result.isEnabled());
    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {
        when(userRepository.findByUsername(USERNAME)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> service.loadUserByUsername(USERNAME));

        assertEquals("User not found", exception.getMessage());
    }

    private User buildUser(boolean isActive) {
        return User.builder()
                .firstName("Liam")
                .lastName("Miller")
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(isActive)
                .build();
    }

}
