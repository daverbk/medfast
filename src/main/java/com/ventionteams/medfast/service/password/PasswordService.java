package com.ventionteams.medfast.service.password;

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
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for handling reset password operations.
 */
@Service
@RequiredArgsConstructor
public class PasswordService {

  private final UserService userService;
  private final PasswordEncoder passwordEncoder;
  private final OneTimePasswordRepository oneTimePasswordRepository;

  /**
   * Reset the password for the user.
   */
  public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
    OneTimePassword token = oneTimePasswordRepository
        .findByUserEmailAndToken(resetPasswordRequest.getEmail(), resetPasswordRequest.getOtp())
        .orElseThrow(() -> new TokenNotFoundException(resetPasswordRequest.getEmail(),
            "One time password token is not found for user"));

    if (passwordEncoder.matches(resetPasswordRequest.getNewPassword(),
        token.getUser().getPassword())) {
      throw new PasswordDoesNotMeetHistoryConstraint("The password has already been used before");
    }

    userService.resetPassword(token.getUser(),
        passwordEncoder.encode(resetPasswordRequest.getNewPassword()));
  }

  /**
   * Change the password for the logged in user.
   */
  public void changePassword(User user, ChangePasswordRequest changePasswordRequest) {
    if (!passwordEncoder.matches(changePasswordRequest.getCurrentPassword(), user.getPassword())) {
      throw new InvalidCurrentPasswordException("Incorrect current password. Please try again");
    }
    if (changePasswordRequest.getCurrentPassword().equals(changePasswordRequest.getNewPassword())) {
      throw new PasswordDoesNotMeetRepetitionConstraint(
          "Current password and new password should be different");
    }

    userService.resetPassword(user,
        passwordEncoder.encode(changePasswordRequest.getNewPassword()));
  }
}
