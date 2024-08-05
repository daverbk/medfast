package com.ventionteams.medfast.service.password;

import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.password.PasswordDoesNotMeetHistoryConstraint;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ResetPasswordServiceTest {
    @Mock
    private UserService userService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private OneTimePasswordRepository oneTimePasswordRepository;

    @InjectMocks
    private ResetPasswordService resetPasswordService;

    @Test
    public void resetPassword_ValidToken_NewPasswordNotInHistory() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("1234");
        request.setNewPassword("newPassword");

        User user = new User();
        user.setPassword("oldPassword");

        OneTimePassword otp = new OneTimePassword();
        otp.setUser(user);

        when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getOtp())).thenReturn(Optional.of(otp));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);

        resetPasswordService.resetPassword(request);

        verify(userService, times(1)).resetPassword(user, passwordEncoder.encode(request.getNewPassword()));
    }

    @Test
    public void resetPassword_TokenNotFound_ExceptionThrown() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("1234");
        request.setNewPassword("newPassword");

        when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getOtp())).thenReturn(Optional.empty());

        assertThrows(TokenNotFoundException.class, () -> resetPasswordService.resetPassword(request));
    }

    @Test
    public void resetPassword_NewPasswordMatchesOldPassword_ExceptionThrown() {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail("test@example.com");
        request.setOtp("1234");
        request.setNewPassword("newPassword");

        User user = new User();
        user.setPassword("newPassword");

        OneTimePassword otp = new OneTimePassword();
        otp.setUser(user);

        when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(), request.getOtp())).thenReturn(Optional.of(otp));
        when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);

        assertThrows(PasswordDoesNotMeetHistoryConstraint.class, () -> resetPasswordService.resetPassword(request));
    }
}
