package com.ventionteams.medfast.exception.auth;

import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the user is not found.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class UserNotFoundException extends NoSuchElementException {

  public UserNotFoundException(String email) {
    super(String.format("User with email '%s' not found.",
        email != null ? email : "unknown"));
  }
}
