package com.ventionteams.medfast.controller;

import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.dto.response.StandardizedResponse;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.password.InvalidCurrentPasswordException;
import com.ventionteams.medfast.exception.password.PasswordDoesNotMeetRepetitionConstraint;
import com.ventionteams.medfast.service.password.PasswordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Password controller responsible for changing user passwords.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Patient password actions")
@RequestMapping("/api/patient/settings/password")
public class PasswordController {

  private final PasswordService passwordService;

  /**
   * Change the user's password.
   */
  @Operation(summary = "Change password")
  @PostMapping("/change")
  public ResponseEntity<StandardizedResponse<String>> changePassword(
      @AuthenticationPrincipal User user,
      @RequestBody @Valid ChangePasswordRequest changePasswordRequest) {

    StandardizedResponse<String> response;

    try {
      passwordService.changePassword(user, changePasswordRequest);
      response = StandardizedResponse.ok(
          "Your password is changed",
          HttpStatus.OK.value(),
          "Operation successful");
    } catch (InvalidCurrentPasswordException | PasswordDoesNotMeetRepetitionConstraint e) {
      response = StandardizedResponse.error(
          HttpStatus.CONFLICT.value(),
          "Password change failed",
          e.getClass().getName(),
          e.getMessage()
      );
    }

    return ResponseEntity.status(response.getStatus()).body(response);
  }
}
