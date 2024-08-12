package com.ventionteams.medfast.exception.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when the password does not meet the repetition constraint.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class PasswordDoesNotMeetRepetitionConstraint extends RuntimeException {

  public PasswordDoesNotMeetRepetitionConstraint(String message) {
    super(message);
  }
}
