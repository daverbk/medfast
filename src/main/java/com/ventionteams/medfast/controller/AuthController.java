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
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

//exceptions
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.mail.MailAuthenticationException;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import org.springframework.security.authentication.DisabledException;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

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
    public ResponseEntity<StandardizedResponse<String>> signUp(@RequestBody @Valid SignUpRequest request) {
        StandardizedResponse<String> response;

        try {
            String signupResponse = authenticationService.signUp(request);
            response =
                new StandardizedResponse<>(
                    signupResponse,
                    HttpStatus.OK.value(),
                    "Sign up successful");
        } catch (MessagingException | IOException |
                 MailAuthenticationException | UserAlreadyExistsException e) {
            // When MailAuthenticationException is thrown, it means that the mail is not sent but the user is created and saved in database
            response =
                new StandardizedResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We ran into an issue while sending a verification email, try again please",
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
            response =
                new StandardizedResponse<>(
                    authenticationResponse,
                    HttpStatus.OK.value(),
                    "Sign in successful");
        } catch (BadCredentialsException | DisabledException e) {
            response =
                new StandardizedResponse<>(
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
            response =
                new StandardizedResponse<>(
                    refreshResponse,
                    HttpStatus.OK.value(),
                    "Refreshing token successful");
        } catch (Exception e) {
            response =
                new StandardizedResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We ran into an issue while refreshing your token, try again please",
                    e.getClass().getName(),
                    e.getMessage());
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Verify email address")
    @PostMapping("/verify")
    public ResponseEntity<StandardizedResponse<String>> verifyUser(@RequestParam("email") String email, @RequestParam("code") String code) {
        StandardizedResponse<String> response;
        try {
            boolean isVerified = verificationTokenService.verify(email, code);


            if (isVerified) {
                response = new StandardizedResponse<>(
                    "Your account is verified",
                    HttpStatus.OK.value(),
                    "Operation successful");
            } else {
                response = new StandardizedResponse<>(
                    HttpStatus.BAD_REQUEST.value(),
                    "Invalid verification code",
                    null,
                    null);
            }
        }
        catch (TokenNotFoundException | UsernameNotFoundException e) {
            response =
                new StandardizedResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We ran into an issue while verifying your email, try again please",
                    e.getClass().getName(),
                    e.getMessage());

        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "Request another verification email")
    @PostMapping("/reverify")
    public ResponseEntity<StandardizedResponse<String>> reverifyUser(@RequestParam("email") String email) {
        StandardizedResponse<String> response;

        try {
            authenticationService.sendVerificationEmail(email);
            String reverifyResponse = "Another email has been sent to your email";
            response = new StandardizedResponse<>(
                reverifyResponse,
                HttpStatus.OK.value(),
                "Operation successful");
        } catch (MessagingException | IOException | UsernameNotFoundException |
                 IncorrectResultSizeDataAccessException | UserIsAlreadyVerifiedException e) {
            // IncorrectResultSizeDataAccessException is a wierd bug where each time it returns incremented
            // amount of users even though there is only one user with that email in database
            response =
                new StandardizedResponse<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "We ran into an issue while sending another verification email, try again please",
                    e.getClass().getName(),
                    e.getMessage());
        }

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
