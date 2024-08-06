package com.ventionteams.medfast.exception.appointment;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class NegativeAppointmentsAmountException extends RuntimeException{
    public NegativeAppointmentsAmountException(String message){
        super(String.format("Failed with: %s", message));
    }
}
