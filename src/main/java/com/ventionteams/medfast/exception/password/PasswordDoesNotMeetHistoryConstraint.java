package com.ventionteams.medfast.exception.password;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class PasswordDoesNotMeetHistoryConstraint extends RuntimeException {
    public PasswordDoesNotMeetHistoryConstraint(String message) {
        super(String.format("Failed with %s", message));
    }
}
