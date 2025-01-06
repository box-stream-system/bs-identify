package com.boxstream.bs_identity.exception;

public class InvalidUUIDFormatException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidUUIDFormatException() {
        super(ErrorCode.INVALID_UUID_FORMAT.getMessage());
        this.errorCode = ErrorCode.INVALID_UUID_FORMAT;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
