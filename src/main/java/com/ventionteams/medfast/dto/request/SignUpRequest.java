package com.ventionteams.medfast.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@Schema(description = "Sign up request")
public class SignUpRequest {

    @Schema(description = "Email", example = "johndoe@gmail.com")
    @Size(min = 10, max = 50, message = "Email must contain from 10 to 50 characters")
    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must follow the format user@example.com")
    private String email;

    @Schema(description = "Password", example = "12312312")
    @Size(min = 10, max = 50, message = "Password's length must not be less than 10 or greater than 50 characters")
    @NotBlank(message = "Password must not be blank")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[!\"#$%&'()*+,\\-./:;<=>?@\\[\\]^_`{|}~])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{10,50}$",
        message = "Password must contain at least one digit, one special character, one lowercase, and one uppercase letter, and no whitespace"
    )
    private String password;

    @Schema(description = "Name", example = "Alex")
    @Size(min = 2, max = 50, message = "Name's length must not be less than 2 or greater than 50 characters")
    @NotBlank(message = "Name must not be blank")
    private String name;

    @Schema(description = "Surname", example = "Smith")
    @Size(min = 2, max = 50, message = "Surname's length must not be less than 2 or greater than 50 characters")
    @NotBlank(message = "Surname must not be blank")
    private String surname;

    @Schema(description = "Date of birth in yyyy-mm-dd", example = "2000-07-09")
    @Past(message = "Birth date must be in the past")
    @NotNull(message = "Birth date must not be blank")
    private LocalDate birthDate;

    @Schema(description = "Street address", example = "123 Main Street")
    @Size(min = 2, max = 50, message = "Street address's length must not be less than 2 or greater than 50 characters")
    @NotBlank(message = "Street address must not be blank")
    private String streetAddress;

    @Schema(description = "House", example = "42 a")
    @Size(min = 1, max = 20, message = "House's length must not be less than 1 or greater than 20 characters")
    @NotBlank(message = "House must not be blank")
    private String house;

    @Schema(description = "Apartment", example = "10")
    @Size(min = 1, max = 20, message = "Apartment's length must not be less than 1 or greater than 20 characters")
    @NotBlank(message = "Apartment must not be blank")
    private String apartment;

    @Schema(description = "City", example = "Chicago")
    @Size(min = 1, max = 50, message = "City's length must not be less than 1 or greater than 50 characters")
    @NotBlank(message = "City must not be blank")
    private String city;

    @Schema(description = "State", example = "Illinois")
    @Size(min = 2, max = 50, message = "State's length must not be less than 1 or greater than 50 characters")
    @NotBlank(message = "State must not be blank")
    private String state;

    @Schema(description = "ZIP", example = "60007")
    @Size(min = 5, max = 5, message = "ZIP's length must be 5 characters")
    @NotBlank(message = "ZIP must not be blank")
    private String zip;

    @Schema(description = "Phone number", example = "15551234567")
    @Size(min = 11, max = 11, message = "Phone number's length must be 11 characters")
    @NotBlank(message = "Phone number must not be blank")
    private String phone;

    @Schema(description = "Legal sex", example = "Male")
    @Size(min = 2, max = 30, message = "Legal sex's length must not be less than 2 or greater than 30 characters")
    @NotBlank(message = "Legal sex must not be blank")
    private String sex;

    @Schema(description = "Citizenship", example = "Canada")
    @Size(min = 2, max = 50, message = "Citizenship's length must not be less than 2 or greater than 50 characters")
    @NotBlank(message = "Citizenship must not be blank")
    private String citizenship;
}
