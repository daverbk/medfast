package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Sign in request")
public class SignInRequest {

    @Schema(description = "Email", example = "johndoe@gmail.com")
    @Size(min = 10, max = 50, message = "Email must contain from 10 to 50 characters")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must follow the format user@example.com")
    private String email;

    @Schema(description = "Password", example = "12312312")
    @Size(min = 10, max = 50, message = "Password's length must not be less than 10 or greater than 50 characters")
    @NotBlank(message = "Password must not be blank")
    private String password;
}
