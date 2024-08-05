package com.ventionteams.medfast.exception.auth;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String token, String message) {
        super(String.format("The token sent has already expired [%s]: %s", token, message));
    }
}
