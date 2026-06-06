package com.gym.crm.exception;

public class IpBlockedException extends AuthenticationException {

    public IpBlockedException(String message) {
        super(message);
    }

    public IpBlockedException(String message, Throwable cause) {
        super(message, cause);
    }

}
