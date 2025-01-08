package com.boxstream.bs_identity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    UNCATEGORIZED_EXCEPTION(1000, "Uncategorized error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1001, "Invalid key", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1002, "User not found", HttpStatus.NOT_FOUND),
    USER_ALREADY_EXISTS(1003, "User already exists", HttpStatus.CONFLICT),
    USERNAME_ALREADY_EXISTS(1004, "Username already exists", HttpStatus.CONFLICT),
    UUID_ALREADY_EXISTS(1005, "UUID already exists", HttpStatus.CONFLICT),
    AUTHENTICATION_FAILED(1006, "Authenticate failed", HttpStatus.UNAUTHORIZED),
    INVALID_UUID_FORMAT(1007, "Invalid UUID format", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1008, "Unauthorized", HttpStatus.FORBIDDEN),
    METHOD_ARGUMENT_NOT_VALID(1009, "Method Argument Not Valid", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1010, "Unauthenticated", HttpStatus.UNAUTHORIZED),

    ;

    private final int code; // error code user define for dev
    private final String message;
    private final HttpStatusCode httpStatusCode; // code for Http response status
}

