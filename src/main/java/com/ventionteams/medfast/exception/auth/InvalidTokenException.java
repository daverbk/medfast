package com.ventionteams.medfast.exception.auth;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown if no Authorization header or it has wrong format.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class InvalidTokenException extends JwtException {

  public InvalidTokenException(String criteria, String message) {
    super(String.format("Error for [%s]: %s", criteria, message));
  }
}
