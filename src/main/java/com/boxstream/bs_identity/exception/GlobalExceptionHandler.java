package com.boxstream.bs_identity.exception;

import com.boxstream.bs_identity.dto.ApiResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  private static final String MIN_ATTRIBUTE = "min";

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ApiResponse> handleRuntimeException(RuntimeException ex) {
    log.error("handleRuntimeException - Unexpected error occurred:", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse> handleGenericException(Exception ex) {
    log.error("Unexpected error occurred:", ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(
            ApiResponse.builder()
                .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .message(ex.getMessage())
                .build());
  }

  @ExceptionHandler({UserNotFoundException.class})
  public ResponseEntity<ApiResponse> handleUserNotFoundExceptions(RuntimeException ex) {
    return ResponseEntity.status(ErrorCode.USER_NOT_FOUND.getHttpStatusCode())
        .body(
            ApiResponse.builder()
                .code(ErrorCode.USER_NOT_FOUND.getCode())
                .message(ErrorCode.USER_NOT_FOUND.getMessage())
                .build());
  }

  @ExceptionHandler({InvalidUUIDFormatException.class})
  public ResponseEntity<ApiResponse> handleInvalidUUIDExceptions(RuntimeException ex) {
    return ResponseEntity.status(ErrorCode.INVALID_UUID_FORMAT.getHttpStatusCode())
        .body(
            ApiResponse.builder()
                .code(ErrorCode.INVALID_UUID_FORMAT.getCode())
                .message(ErrorCode.INVALID_UUID_FORMAT.getMessage())
                .build());
  }

  @ExceptionHandler(UsernameExistsException.class)
  public ResponseEntity<ApiResponse> handleUsernameExistsException(UsernameExistsException ex) {
    return ResponseEntity.status(ErrorCode.USERNAME_ALREADY_EXISTS.getHttpStatusCode())
        .body(
            ApiResponse.builder()
                .code(ErrorCode.USERNAME_ALREADY_EXISTS.getCode())
                .message(ErrorCode.USERNAME_ALREADY_EXISTS.getMessage())
                .build());
  }

  @ExceptionHandler(AuthenticationFailedException.class)
  public ResponseEntity<ApiResponse> handleAuthenticationFailedException(
      AuthenticationFailedException ex) {
    return ResponseEntity.status(ErrorCode.AUTHENTICATION_FAILED.getHttpStatusCode())
        .body(
            ApiResponse.builder()
                .code(ErrorCode.AUTHENTICATION_FAILED.getCode())
                .message(ErrorCode.AUTHENTICATION_FAILED.getMessage())
                .build());
  }

  /*
     ALl Exception to Validation will be thrown via MethodArgumentNotValidException.class
     Then we need custom this function for extract all exception type here

  */
  @ExceptionHandler(value = AccessDeniedException.class)
  public ResponseEntity<ApiResponse> handleAccessDeniedException(AccessDeniedException ex) {
    return ResponseEntity.status(ErrorCode.UNAUTHORIZED.getHttpStatusCode())
        .body(
            ApiResponse.builder()
                .code(ErrorCode.UNAUTHORIZED.getCode())
                .message(ErrorCode.UNAUTHORIZED.getMessage())
                .build());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse> handleValidationException(
      MethodArgumentNotValidException exception) {

    // Build response data
    List<Map<String, Object>> errors =
        exception.getBindingResult().getAllErrors().stream()
            .map(
                error -> {
                  String field = ((FieldError) error).getField();
                  String message =
                      error.getDefaultMessage(); // Get the detailed message set in the validator

                  // Build each error detail
                  Map<String, Object> errorDetails = new HashMap<>();
                  errorDetails.put("field", field);
                  errorDetails.put("message", message);
                  return errorDetails;
                })
            .toList();

    return ResponseEntity.badRequest()
        .body(
            ApiResponse.builder()
                .code(ErrorCode.VALIDATION_FAILED.getCode())
                .message(ErrorCode.VALIDATION_FAILED.getMessage())
                .data(errors)
                .build());
  }

  private String mapAttribute(String message, Map<String, Object> attributes) {
    for (Map.Entry<String, Object> entry : attributes.entrySet()) {
      message = message.replace("{" + entry.getKey() + "}", entry.getValue().toString());
    }
    return message;
  }
}
