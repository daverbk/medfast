package com.ventionteams.medfast.service.password;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
import com.ventionteams.medfast.entity.OneTimePassword;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.exception.password.InvalidCurrentPasswordException;
import com.ventionteams.medfast.exception.password.PasswordDoesNotMeetHistoryConstraint;
import com.ventionteams.medfast.exception.password.PasswordDoesNotMeetRepetitionConstraint;
import com.ventionteams.medfast.repository.OneTimePasswordRepository;
import com.ventionteams.medfast.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Checks the reset password service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {

  @Mock
  private UserService userService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private OneTimePasswordRepository oneTimePasswordRepository;

  @InjectMocks
  private PasswordService passwordService;

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

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(),
        request.getOtp())).thenReturn(Optional.of(otp));
    when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(false);

    passwordService.resetPassword(request);

    verify(userService, times(1)).resetPassword(user,
        passwordEncoder.encode(request.getNewPassword()));
  }

  @Test
  public void resetPassword_TokenNotFound_ExceptionThrown() {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setEmail("test@example.com");
    request.setOtp("1234");
    request.setNewPassword("newPassword");

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(),
        request.getOtp())).thenReturn(Optional.empty());

    assertThrows(TokenNotFoundException.class, () -> passwordService.resetPassword(request));
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

    when(oneTimePasswordRepository.findByUserEmailAndToken(request.getEmail(),
        request.getOtp())).thenReturn(Optional.of(otp));
    when(passwordEncoder.matches(request.getNewPassword(), user.getPassword())).thenReturn(true);

    assertThrows(PasswordDoesNotMeetHistoryConstraint.class,
        () -> passwordService.resetPassword(request));
  }

  @Test
  public void changePassword_InvalidCurrentPassword_ExceptionThrown() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");
    User user = User.builder().password("123123123").build();

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        false);

    assertThrows(InvalidCurrentPasswordException.class,
        () -> passwordService.changePassword(user, request));
  }

  @Test
  public void changePassword_NewPasswordEqualsCurrentPassword_ExceptionThrown() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("password");
    request.setNewPassword("password");
    User user = User.builder().password("password").build();

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        true);

    assertThrows(PasswordDoesNotMeetRepetitionConstraint.class,
        () -> passwordService.changePassword(user, request));
  }

  @Test
  public void changePassword_CorrectRequest_resetPasswordInvoked() {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("currentPassword");
    request.setNewPassword("newPassword");

    User user = User.builder().password("currentPassword").build();

    when(passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())).thenReturn(
        true);

    passwordService.changePassword(user, request);

    verify(userService, times(1)).resetPassword(user,
        passwordEncoder.encode(request.getNewPassword()));
  }
}
