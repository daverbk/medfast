package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.mail.MailAuthenticationException;
import java.io.IOException;
import jakarta.mail.MessagingException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;


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
    private final TokenConfig tokenConfig;


    @Transactional(rollbackFor={ MessagingException.class, IOException.class})
    public String signUp(SignUpRequest request) throws MessagingException, IOException {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userService.create(request);
        sendVerificationEmail(user.getEmail());
        return "Email verification link has been sent to your email";
    }

    public void sendVerificationEmail(String email) throws MessagingException, IOException {
        User user = userService.getUserByEmail(email);
        if (user.isEnabled()) {
            throw new UserIsAlreadyVerifiedException(email, "User is already verified");
        }
        verificationTokenService.addVerificationTokenForUser(user.getEmail());

        int maxRetries = 3;
        int retries = 0;
        boolean mailSuccessfullySent = false;
        while (!mailSuccessfullySent && retries < maxRetries) {
            try {
                emailService.sendVerificationEmail(user);
                mailSuccessfullySent = true;
            } catch (MailAuthenticationException e) {
                retries++;
                if (retries == maxRetries) {
                    throw e;
                }
            }
        }
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
            tokenConfig.timeout().access(),
            tokenConfig.timeout().refresh());
    }
}
