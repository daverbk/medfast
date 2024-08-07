package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the user already exists in the database.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class UserAlreadyExistsException extends RuntimeException {

  public UserAlreadyExistsException(String userCredential, String message) {
    super(String.format("Failed for [%s]: %s", userCredential, message));
  }
}
