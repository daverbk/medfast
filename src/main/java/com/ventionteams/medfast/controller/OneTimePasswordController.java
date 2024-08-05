package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.service.password.OneTimePasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "One-time Password")
@RequestMapping("/auth/otp")
public class OneTimePasswordController {
    private final OneTimePasswordService oneTimePasswordService;

    @Operation(summary = "Request one time password")
    @PostMapping
    public ResponseEntity<String> sendOtp(@Email @RequestParam("email") String email) {
        try {
            oneTimePasswordService.sendResetPasswordEmail(email);
            return ResponseEntity.ok("Reset password email has been sent");
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("We ran into an issue while sending a verification email, try again please");
        }
    }

    @Operation(summary = "Verify one time password")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyOtp(@Email @RequestParam("email") String email,
                                            @Size(min = 4, max = 4) @RequestParam("token") String token) {

        oneTimePasswordService.verify(email, token);
        return ResponseEntity.ok("Token is verified");
    }
}
