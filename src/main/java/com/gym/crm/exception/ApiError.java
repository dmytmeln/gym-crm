package com.gym.crm.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiError {

    VALIDATION_ERROR(2760, "Validation error", HttpStatus.BAD_REQUEST),
    AUTHENTICATION_ERROR(2805, "Authentication fails", HttpStatus.UNAUTHORIZED),
    AUTHORIZATION_ERROR(2806, "User is not authorized for request operation", HttpStatus.FORBIDDEN),
    NOT_FOUND_ERROR(2835, "Requested data was not found", HttpStatus.NOT_FOUND),
    SERVICE_ERROR(3200, "Internal processing error", HttpStatus.INTERNAL_SERVER_ERROR),
    DATABASE_ERROR(3358, "Database error", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus status;

    ApiError(int code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

}
