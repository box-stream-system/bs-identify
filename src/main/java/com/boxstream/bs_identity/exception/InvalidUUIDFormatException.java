package com.boxstream.bs_identity.exception;

import lombok.Getter;

@Getter
public class InvalidUUIDFormatException extends RuntimeException {
    private final ErrorCode errorCode;

    public InvalidUUIDFormatException() {
        super(ErrorCode.INVALID_UUID_FORMAT.getMessage());
        this.errorCode = ErrorCode.INVALID_UUID_FORMAT;
    }

}
