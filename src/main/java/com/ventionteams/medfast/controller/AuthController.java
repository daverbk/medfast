package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.RefreshTokenRequest;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.service.auth.AuthenticationService;
import com.ventionteams.medfast.service.auth.RefreshTokenService;
import com.ventionteams.medfast.service.auth.VerificationTokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.mail.MailAuthenticationException;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import org.springframework.security.authentication.DisabledException;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

@RestController
@Validated
@RequiredArgsConstructor
@Tag(name = "Sign in")
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationService authenticationService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;

    @Operation(summary = "Sign up")
    @PostMapping("/signup")
    public ResponseEntity<StandardizedResponse<String>> signUp(@RequestBody @Valid SignUpRequest request) {
        StandardizedResponse<String> response;

        try {
            String signupResponse = authenticationService.signUp(request);
            response = StandardizedResponse.ok(
                    signupResponse,
                    HttpStatus.OK.value(),
                    "Sign up successful");
        }  catch (MessagingException | IOException |
                  MailAuthenticationException | UserAlreadyExistsException e) {
            response = StandardizedResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Sign up failed. Please, try again later or contact our support team.",
                    e.getClass().getName(),
                    e.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Sign in")
    @PostMapping("/signin")
    public ResponseEntity<StandardizedResponse<JwtAuthenticationResponse>> signIn(@RequestBody @Valid SignInRequest request) {
        StandardizedResponse<JwtAuthenticationResponse> response;

        try{
            JwtAuthenticationResponse authenticationResponse = authenticationService.signIn(request);
            response = StandardizedResponse.ok(
                    authenticationResponse,
                    HttpStatus.OK.value(),
                    "Sign in successful");
        } catch (BadCredentialsException | DisabledException e) {
            response = StandardizedResponse.error(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Provided credentials are bad or user is disabled",
                    e.getClass().getName(),
                    e.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Refresh access token")
    @PostMapping("/refresh")
    public ResponseEntity<StandardizedResponse<JwtAuthenticationResponse>> refreshToken(@RequestBody @Valid RefreshTokenRequest request) {
        StandardizedResponse<JwtAuthenticationResponse> response;
        try{
            JwtAuthenticationResponse refreshResponse = refreshTokenService.refreshToken(request);
            response = StandardizedResponse.ok(
                    refreshResponse,
                    HttpStatus.OK.value(),
                    "Refreshing token successful");
        } catch (Exception e) {
            response = StandardizedResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "Refreshing token failed",
                    e.getClass().getName(),
                    e.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Verify email address")
    @PostMapping("/verify")
    public ResponseEntity<StandardizedResponse<String>> verifyUser(@Email @RequestParam("email") String email, @RequestParam("code") String code) {
        StandardizedResponse<String> response;
        try {
            boolean isVerified = verificationTokenService.verify(email, code);
            if (isVerified) {
                response = StandardizedResponse.ok(
                    "Your account is verified",
                    HttpStatus.OK.value(),
                    "Operation successful");
            } else {
                response = StandardizedResponse.error(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid verification code",
                    null,
                    null);
            }
        }
        catch (TokenNotFoundException | UsernameNotFoundException e) {
            response = StandardizedResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We ran into an issue while verifying your email, try again please",
                    e.getClass().getName(),
                    e.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Request another verification email")
    @PostMapping("/reverify")
    public ResponseEntity<StandardizedResponse<String>> reverifyUser(@Email @RequestParam("email") String email) {
        StandardizedResponse<String> response;
        try {
            authenticationService.sendVerificationEmail(email);
            response = StandardizedResponse.ok(
                "Another email has been sent to your email",
                HttpStatus.OK.value(),
                "Operation successful");
        } catch (MessagingException | IOException | UsernameNotFoundException
                 | MailAuthenticationException | UserIsAlreadyVerifiedException e) {
            response = StandardizedResponse.error(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We ran into an issue while sending another verification email, try again please",
                    e.getClass().getName(),
                    e.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
