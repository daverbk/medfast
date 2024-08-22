package com.ventionteams.medfast.exception.medicaltest;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the medical test data is invalid.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMedicalTestDataException extends RuntimeException {
  public InvalidMedicalTestDataException(String message)  {
    super(String.format("Invalid test data: %s", message));
  }
}
