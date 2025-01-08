package com.boxstream.bs_identity.exception;

import com.boxstream.bs_identity.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException e) {

        logger.error(e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(
                        ApiResponse.builder()
                                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                                .message(e.getMessage())
                                .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build());
    }

    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<ApiResponse> handleUserNotFoundExceptions(RuntimeException ex) {
        ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler({InvalidUUIDFormatException.class})
    public ResponseEntity<ApiResponse> handleInvalidUUIDExceptions(RuntimeException ex) {
        ErrorCode errorCode = ErrorCode.INVALID_UUID_FORMAT;
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<ApiResponse> handleUsernameExistsException(UsernameExistsException ex) {
        ErrorCode errorCode = ErrorCode.USER_ALREADY_EXISTS;
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());

        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(AuthenticationFailedException.class)
    public ResponseEntity<ApiResponse> handleAuthenticationFailedException(AuthenticationFailedException ex) {
        ErrorCode errorCode = ErrorCode.AUTHENTICATION_FAILED;
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());
        return ResponseEntity
                .status(errorCode.getHttpStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorCode errorCode = ErrorCode.METHOD_ARGUMENT_NOT_VALID;

        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).
                body(ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build());
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).
                body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }
}

