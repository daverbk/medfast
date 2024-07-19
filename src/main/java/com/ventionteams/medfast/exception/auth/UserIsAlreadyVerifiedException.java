package com.ventionteams.medfast.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class UserIsAlreadyVerifiedException extends RuntimeException {
    public UserIsAlreadyVerifiedException(String userCredential, String message) {
        super(String.format("User with credential [%s]: %s", userCredential, message));
    }
}
