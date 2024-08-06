package com.ventionteams.medfast.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Standardized response structure")
public class StandardizedResponse<T> {

    @Schema(description = "Value of generic type T", example = "JwtAuthenticationResponse")
    private T data;

    @Schema(description = "HTTP status code", example = "200")
    private int status;

    @Schema(description = "Message", example = "Sign up successful")
    private String message;

    @Schema(description = "Timestamp", example = "2024-07-31T14:00:00.0000000")
    private LocalDateTime timestamp;

    @Schema(description = "Error class", example = "com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException")
    private String errorClass;

    @Schema(description = "Error message", example = "User with credential [johndoe@gmail.com]: User is already verified")
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

    public StandardizedResponse(T data, int status, String message, String errorClass, String errorMessage) {
        this.errorClass = errorClass;
        this.errorMessage = errorMessage;
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public static <T> StandardizedResponse<T> ok(T data, int status, String message) {
        return new StandardizedResponse<>(data, status, message);
    }

    public static <T> StandardizedResponse<T> error(int status, String message, String errorClass, String errorMessage) {
        return new StandardizedResponse<>(status, message, errorClass, errorMessage);
    }

    public static <T> StandardizedResponse<T> errorWithData(T data, int status, String message, String errorClass, String errorMessage) {
        return new StandardizedResponse<>(data, status, message, errorClass, errorMessage);
    }

}
