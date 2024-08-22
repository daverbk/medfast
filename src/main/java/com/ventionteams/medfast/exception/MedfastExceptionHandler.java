package com.ventionteams.medfast.exception;

import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.exception.auth.InvalidTokenException;
import com.ventionteams.medfast.exception.auth.UserNotFoundException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.validation.ConstraintViolationException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Global exception handler.
 */
@RestControllerAdvice
public class MedfastExceptionHandler {

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected StandardizedResponse<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach((error) -> {
      String fieldName = error.getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });
    return StandardizedResponse.error(errors, HttpStatus.BAD_REQUEST.value(), "Validation failed",
        ex.getClass().getName(), ex.getMessage());
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(ConstraintViolationException.class)
  protected StandardizedResponse<Map<String, String>> handleConstraintViolation(
      ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach((error) -> {
      String propertyPath = error.getPropertyPath().toString();
      String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
      String errorMessage = error.getMessage();
      errors.put(fieldName, errorMessage);
    });
    return StandardizedResponse.error(errors, HttpStatus.BAD_REQUEST.value(), "Validation failed",
        ex.getClass().getName(), ex.getMessage());
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({InterruptedException.class})
  protected StandardizedResponse<Map<String, String>> handleAsyncExceptions(
      InterruptedException ex) {

    return StandardizedResponse.error(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "The operation was interrupted.",
        ex.getClass().getName(),
        ex.getMessage()
    );
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  protected StandardizedResponse<?> handleTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    StandardizedResponse<?> response;

    if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
      String validValues = String.join(", ", getEnumValues(ex.getRequiredType()));
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid '" + ex.getName() + "' parameter value",
          ex.getClass().getName(),
          String.format("Expected one of: %s", validValues)
      );
    } else {
      response = StandardizedResponse.error(
          HttpStatus.BAD_REQUEST.value(),
          "Invalid parameter",
          ex.getClass().getName(),
          "Invalid value provided for parameter '" + ex.getName() + "'"
      );
    }

    return response;
  }

  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  @ExceptionHandler({
      MalformedJwtException.class,
      SignatureException.class,
      InvalidTokenException.class,
      ExpiredJwtException.class
  })
  protected StandardizedResponse<?> handleJwtException(
      JwtException ex) {
    StandardizedResponse<?> response;

    response = StandardizedResponse.error(
        HttpStatus.UNAUTHORIZED.value(),
        "Unauthorized",
        ex.getClass().getName(),
        ex.getMessage()
    );

    return response;
  }

  @ResponseStatus(HttpStatus.NOT_FOUND)
  @ExceptionHandler({UserNotFoundException.class})
  protected StandardizedResponse<?> handleUserNotFoundException(
      RuntimeException ex) {
    StandardizedResponse<?> response;

    response = StandardizedResponse.error(
        HttpStatus.NOT_FOUND.value(),
        "User not found",
        ex.getClass().getName(),
        ex.getMessage()
    );

    return response;
  }

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler({Exception.class})
  protected StandardizedResponse<?> handleInternalServerError(
      Exception ex) {
    StandardizedResponse<?> response;

    response = StandardizedResponse.error(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal server error",
        ex.getClass().getName(),
        ex.getMessage()
    );

    return response;
  }

  private List<String> getEnumValues(Class<?> enumType) {
    return Arrays.stream(enumType.getEnumConstants())
        .map(Object::toString)
        .toList();
  }
}
