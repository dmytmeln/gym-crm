package com.gym.crm.exception;

public abstract class GymCrmException extends RuntimeException {

    protected GymCrmException(String message) {
        super(message);
    }

    protected GymCrmException(String message, Throwable cause) {
        super(message, cause);
    }

}
