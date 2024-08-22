package com.ventionteams.medfast.exception.medicaltest;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when user credentials are bad.
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class BadCredentialsForMedicalTest extends RuntimeException {
  public BadCredentialsForMedicalTest(String message) {
    super(String.format("Invalid test data: %s", message));
  }

}
