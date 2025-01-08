package com.boxstream.bs_identity.exception;

import lombok.Getter;

@Getter
public class AuthenticationFailedException extends RuntimeException {
    private final ErrorCode errorCode;

    public AuthenticationFailedException() {
        super(ErrorCode.AUTHENTICATION_FAILED.getMessage());
        this.errorCode = ErrorCode.AUTHENTICATION_FAILED;
    }

}