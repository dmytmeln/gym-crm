package com.gym.crm.security;

import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class SecurityContext {

    private static UserCredentials currentUser;

    private SecurityContext() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void setCurrentUser(UserCredentials userCredentials) {
        log.debug("Setting current user in security context: {}", userCredentials.username());
        currentUser = userCredentials;
    }

    public static UserCredentials getCurrentUser() {
        log.debug("Getting current user from security context: {}", getUsername(currentUser));
        return currentUser;
    }

    public static void clear() {
        UserCredentials removed = currentUser;
        currentUser = null;
        log.debug("Cleared security context, removed user: {}", getUsername(removed));
    }

    private static String getUsername(UserCredentials userCredentials) {
        return Optional.ofNullable(userCredentials)
                .map(UserCredentials::username)
                .orElse("null");
    }

}
