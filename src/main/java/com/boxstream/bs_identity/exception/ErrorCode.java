package com.boxstream.bs_identity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    USER_NOT_FOUND(404, "User not found"),
    USER_ALREADY_EXISTS(409, "User already exists"),
    USERNAME_ALREADY_EXISTS(409, "Username already exists"),
    UUID_ALREADY_EXISTS(409, "UUID already exists"),
    AUTHENTICATION_FAILED(301, "Authenticate failed"),
    INVALID_UUID_FORMAT(400, "Invalid UUID format");

    private final int code;
    private final String message;
}

