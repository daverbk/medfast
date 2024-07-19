package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.service.auth.AuthenticationService;
import com.ventionteams.medfast.service.auth.RefreshTokenService;
import com.ventionteams.medfast.service.auth.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Sign in")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;

    @Operation(summary = "Sign up")
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody @Valid SignUpRequest request) {
        ResponseEntity<String> response;

        try {
            response = ResponseEntity.ok(authenticationService.signUp(request));
        } catch (MessagingException | IOException e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("We ran into an issue while sending a verification email, try again please");
        }

        return response;
    }

    @Operation(summary = "Sign in")
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody @Valid SignInRequest request) {
        return ResponseEntity.ok(authenticationService.signIn(request));
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(refreshTokenService.refreshToken(request));
    }

    @Operation(summary = "Verify email address")
    @PostMapping("/verify")
    public ResponseEntity<String> verifyUser(@RequestParam("email") String email, @RequestParam("code") String code) {
        return verificationTokenService.verify(email, code)
            ? ResponseEntity.ok("Your account is verified")
            : ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid verification code");
    }

    @Operation(summary = "Request another verification email")
    @PostMapping("/reverify")
    public ResponseEntity<String> reverifyUser(@RequestParam("email") String email) {
        ResponseEntity<String> response;

        try {
            authenticationService.sendVerificationEmail(email);
            response = ResponseEntity.ok("Another email has been sent to your email");
        } catch (MessagingException | IOException e) {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("We ran into an issue while sending a verification email, try again please");
        }

        return response;
    }
}
