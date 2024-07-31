package com.ventionteams.medfast.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StandardizedResponse<T> {
    private T data;
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private String errorClass;
    private String errorMessage;

    public StandardizedResponse(T data, int status, String message) {
        this.data = data;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public StandardizedResponse(int status, String message, String errorClass, String errorMessage) {
        this.errorClass = errorClass;
        this.errorMessage = errorMessage;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }
}
