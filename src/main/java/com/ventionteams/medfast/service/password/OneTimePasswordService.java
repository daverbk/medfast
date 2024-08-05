package com.ventionteams.medfast.service.password;

import com.ventionteams.medfast.config.properties.TokenConfig;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.EmailService;
import com.ventionteams.medfast.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class OneTimePasswordService {
    private final OneTimePasswordRepository oneTimePasswordRepository;
    private final EmailService emailService;
    private final UserService userService;
    private final Random random;
    private final TokenConfig tokenConfig;

    public void verify(String email, String otpToken) {
        OneTimePassword otp = oneTimePasswordRepository.findByUserEmailAndToken(email, otpToken)
            .orElseThrow(() -> new TokenNotFoundException(email, "One time password token is not found for user"));

        long actualValidityPeriod = Duration.between(otp.getCreatedDate(), LocalDateTime.now()).getSeconds();
        if (actualValidityPeriod > tokenConfig.timeout().resetPassword()) {
            oneTimePasswordRepository.delete(otp);
            throw new TokenExpiredException(otpToken, "One time password token is expired");
        }
    }

    public void sendResetPasswordEmail(String email) throws MessagingException {
        User user = userService.getUserByEmail(email);
        emailService.sendResetPasswordEmail(user, generate(user).getToken());
    }

    public OneTimePassword generate(User user) {
        String otpToken = String.format("%04d", random.nextInt(10000));
        OneTimePassword otp = new OneTimePassword();
        otp.setUser(user);
        otp.setToken(otpToken);
        oneTimePasswordRepository.save(otp);
        return otp;
    }
}
