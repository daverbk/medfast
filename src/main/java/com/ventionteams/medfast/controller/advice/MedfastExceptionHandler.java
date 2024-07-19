package com.ventionteams.medfast.controller.advice;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MedfastExceptionHandler {
    @ExceptionHandler(UsernameNotFoundException.class)
    public void handleException(UsernameNotFoundException exc) {
    }
}
