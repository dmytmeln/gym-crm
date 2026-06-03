package com.gym.crm.exception;

public class UserDeactivatedException extends RuntimeException {

    public UserDeactivatedException(String message) {
        super(message);
    }

    public UserDeactivatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
