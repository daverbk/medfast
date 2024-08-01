package com.ventionteams.medfast.service.auth;

import com.ventionteams.medfast.config.properties.VerificationCodeConfig;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.entity.VerificationToken;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.auth.UserIsAlreadyVerifiedException;
import com.ventionteams.medfast.repository.VerificationTokenRepository;
import com.ventionteams.medfast.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationTokenService {
    private final VerificationTokenRepository verificationTokenRepository;
    private final UserService userService;
    private final VerificationCodeConfig verificationCodeConfig;

    @Transactional
    public void addVerificationTokenForUser(String email) {
        UUID securityCode = UUID.randomUUID();
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(securityCode.toString());
        verificationToken.setUser(userService.getUserByEmail(email));
        verificationTokenRepository.save(verificationToken);
    }

    @Transactional
    public boolean verify(String email, String verificationToken) {
        String decodedEmail = URLDecoder.decode(email, StandardCharsets.UTF_8);
        User user = userService.getUserByEmail(decodedEmail);

        if (validateToken(decodedEmail, verificationToken)) {
            if (user.isEnabled()) {
                deleteVerificationToken(decodedEmail);
                throw new UserIsAlreadyVerifiedException(decodedEmail, "User is already verified");
            }

            user.setEnabled(true);
            userService.save(user);
            deleteVerificationToken(decodedEmail);
            return true;
        } else {
            return false;
        }
    }

    public boolean validateToken(String email, String verificationToken) {
        VerificationToken token = getVerificationTokenByUserEmail(email);
        long actualValidityPeriod = Duration.between(token.getCreatedDate(), LocalDateTime.now()).getSeconds();
        if (actualValidityPeriod > verificationCodeConfig.timeout()) {
            deleteVerificationToken(email);
            return false;
        }

        return token.getToken().equals(verificationToken);
    }

    public VerificationToken getVerificationTokenByUserEmail(String email) {
        return verificationTokenRepository.findByUserEmail(email)
            .orElseThrow(() -> new TokenNotFoundException(email, "Token not found"));
    }

    public void deleteVerificationToken(String email) {
        VerificationToken token = getVerificationTokenByUserEmail(email);
        verificationTokenRepository.deleteById(token.getId());
    }
}
