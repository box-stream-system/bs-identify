package com.boxstream.bs_identity.exception;

import com.boxstream.bs_identity.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;


@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);


    // DEFAULT

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        logger.error("Unexpected error occurred:", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred. Please try again later.");
    }

    // CUSTOM

    @ExceptionHandler({UserNotFoundException.class, InvalidUUIDFormatException.class})
    public ResponseEntity<ApiResponse> handleSpecificExceptions(RuntimeException ex) {
        ErrorCode errorCode = getErrorCodeFromException(ex);
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getCode()).body(apiResponse);
    }

    @ExceptionHandler(UsernameExistsException.class)
    public ResponseEntity<ApiResponse> handleUsernameExistsException(UsernameExistsException ex) {
        ErrorCode errorCode = getErrorCodeFromException(ex);
        logger.error("{}: {}", errorCode.name(), errorCode.getMessage());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getCode()).body(apiResponse);
    }

    private ErrorCode getErrorCodeFromException(RuntimeException ex) {
        if (ex instanceof UserNotFoundException) {
            return ((UserNotFoundException) ex).getErrorCode();
        } else if (ex instanceof InvalidUUIDFormatException) {
            return ((InvalidUUIDFormatException) ex).getErrorCode();
        } else if (ex instanceof UsernameExistsException) {
            return ((UsernameExistsException) ex).getErrorCode();
        }
        throw new IllegalStateException("Unhandled exception type: " + ex.getClass().getName());
    }


    /*
        Custom for user creation validate @Valid
        Exception: MethodArgumentNotValidException.class
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        logger.error(String.valueOf(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage());
    }

    // Custom more here
}

