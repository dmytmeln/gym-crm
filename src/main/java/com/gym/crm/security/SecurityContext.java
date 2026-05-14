package com.gym.crm.security;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SecurityContext {

    private static final ThreadLocal<UserCredentials> currentUser = new ThreadLocal<>();

    private SecurityContext() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static void setCurrentUser(UserCredentials userCredentials) {
        log.debug("Setting current user in security context: {}", userCredentials.username());
        currentUser.set(userCredentials);
    }

    public static UserCredentials getCurrentUser() {
        UserCredentials user = currentUser.get();
        log.debug("Getting current user from security context: {}", user != null ? user.username() : "null");
        return user;
    }

    public static void clear() {
        UserCredentials removed = currentUser.get();
        currentUser.remove();
        log.debug("Cleared security context, removed user: {}", removed != null ? removed.username() : "null");
    }

}
