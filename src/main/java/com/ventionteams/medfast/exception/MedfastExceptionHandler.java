package com.ventionteams.medfast.exception;

import com.ventionteams.medfast.dto.response.StandardizedResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class MedfastExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    protected Map<String, String> handleConstraintViolation(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach((error) -> {
            String propertyPath = error.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<StandardizedResponse<?>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
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


        return ResponseEntity.status(response.getStatus()).body(response);
    }

    private List<String> getEnumValues(Class<?> enumType) {
        return Arrays.stream(enumType.getEnumConstants())
            .map(Object::toString)
            .toList();
    }
}
