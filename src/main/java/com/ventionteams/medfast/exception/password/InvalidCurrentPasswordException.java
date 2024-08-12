package com.ventionteams.medfast.exception.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the password is invalid.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidCurrentPasswordException extends RuntimeException {

  public InvalidCurrentPasswordException(String message) {
    super(message);
  }
}
