package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.dto.request.SignInRequest;
import com.ventionteams.medfast.dto.request.SignUpRequest;
import com.ventionteams.medfast.dto.response.JwtAuthenticationResponse;
import com.ventionteams.medfast.entity.RefreshToken;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.UserAlreadyExistsException;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
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
    private final PlatformTransactionManager transactionManager;


    public String signUp(SignUpRequest request) throws MessagingException, IOException {
        request.setPassword(passwordEncoder.encode(request.getPassword()));
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            User user = userService.create(request);
            sendVerificationEmail(user);
            transactionManager.commit(status);
            return "Email verification link has been sent to your email";
        }
        catch (MessagingException | IOException |
               MailAuthenticationException | UserAlreadyExistsException e) {
            transactionManager.rollback(status);
            throw e;
        }
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
            jwtService.getExpirationSeconds(),
            refreshTokenService.getExpirationSeconds());
    }
}
