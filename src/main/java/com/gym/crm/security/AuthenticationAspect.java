package com.gym.crm.security;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Aspect
@Component
@Slf4j
public class AuthenticationAspect {

    @Pointcut("@annotation(authenticated)")
    void annotatedMethod(Authenticated authenticated) {
    }

    @Before(value = "annotatedMethod(authenticated) && args(username, ..)", argNames = "authenticated,username")
    public void checkAuthentication(Authenticated authenticated, String username) {
        UserCredentials currentUser = SecurityContext.getCurrentUser();
        if (currentUser == null) {
            throw new AuthenticationException("User is not authenticated");
        }

        boolean hasValidRole = Arrays.asList(authenticated.value()).contains(currentUser.role());
        if (!Objects.equals(currentUser.username(), username) || !hasValidRole) {
            throw new AuthenticationException("User is not authenticated");
        }
    }

}
