package com.boxstream.bs_identity.exception;

import lombok.Getter;

@Getter
public class UsernameExistsException extends RuntimeException {
  private final ErrorCode errorCode;

  public UsernameExistsException() {
    super(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage());

    this.errorCode = ErrorCode.USERNAME_ALREADY_EXISTS;
  }
}
