package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenService verificationTokenService;

    public String signUp(SignUpRequest request) throws MessagingException, IOException {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userService.create(request);
        sendVerificationEmail(user);
        return "Email verification link has been sent to your email";
    }

    public void sendVerificationEmail(String encodedEmail) throws MessagingException, IOException {
        String decodedEmail = URLDecoder.decode(encodedEmail, StandardCharsets.UTF_8);
        User user = userService.getUserByEmail(decodedEmail);
        sendVerificationEmail(user);
    }

    public void sendVerificationEmail(User user) throws MessagingException, IOException {
        if (user.isEnabled()) {
            throw new UserIsAlreadyVerifiedException(user.getEmail(), "User is already verified");
        }
        
        verificationTokenService.addVerificationTokenForUser(user.getEmail());
        emailService.sendVerificationEmail(user);
    }

    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            ));

        UserDetails user = userService
            .getUserDetailsService()
            .loadUserByUsername(request.getEmail());

        String jwt = jwtService.generateToken(user);
        RefreshToken refreshToken = refreshTokenService.generateToken(user);

        return new JwtAuthenticationResponse(
            jwt,
            refreshToken.getToken(),
            jwtService.getExpirationSeconds(),
            refreshTokenService.getExpirationSeconds());
    }
}
