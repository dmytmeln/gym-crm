package com.gym.crm.aspect;

import com.gym.crm.exception.AuthenticationException;
import com.gym.crm.exception.UserDeactivatedException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityExceptionTranslationAspectTest {

    @InjectMocks
    private SecurityExceptionTranslationAspect aspect;

    @Test
    void shouldProceedWhenNoExceptionIsThrown() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);
        Object expectedResult = new Object();

        when(joinPoint.proceed()).thenReturn(expectedResult);

        Object actualResult = aspect.translateSecurityExceptions(joinPoint);

        assertEquals(expectedResult, actualResult);
        verify(joinPoint).proceed();
    }

    @Test
    void shouldTranslateDisabledExceptionToUserDeactivatedException() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

        when(joinPoint.proceed()).thenThrow(new DisabledException("Disabled"));

        UserDeactivatedException exception = assertThrows(UserDeactivatedException.class, () -> aspect.translateSecurityExceptions(joinPoint));

        assertEquals("User account is deactivated. Please contact support.", exception.getMessage());
    }

    @Test
    void shouldTranslateBadCredentialsExceptionToAuthenticationException() throws Throwable {
        ProceedingJoinPoint joinPoint = mock(ProceedingJoinPoint.class);

        when(joinPoint.proceed()).thenThrow(new BadCredentialsException("Bad credentials"));

        AuthenticationException exception = assertThrows(AuthenticationException.class, () -> aspect.translateSecurityExceptions(joinPoint));

        assertEquals("Invalid username or password", exception.getMessage());
    }

}
