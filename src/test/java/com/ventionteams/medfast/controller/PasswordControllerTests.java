package com.ventionteams.medfast.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.dto.request.ChangePasswordRequest;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.exception.password.InvalidCurrentPasswordException;
import com.ventionteams.medfast.exception.password.PasswordDoesNotMeetRepetitionConstraint;
import com.ventionteams.medfast.service.password.PasswordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Tests the password controller functionality with integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(PostgreContainerExtension.class)
public class PasswordControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PasswordService passwordService;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  public void changePassword_GoodRequest_ReturnsOk() throws Exception {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("qweRTY123$");
    request.setNewPassword("qwerty123ASD!");
    User mockUser = mock(User.class);

    doNothing().when(passwordService).changePassword(mockUser, request);

    ResultActions response = mockMvc.perform(post("/api/patient/settings/password/change")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(user(mockUser)));

    response.andExpect(status().isOk())
        .andExpect(jsonPath("$.status").value(200))
        .andExpect(jsonPath("$.data").value("Your password is changed"));
  }

  @Test
  public void changePassword_InvalidCurrentPassword_ReturnsConflictRequest() throws Exception {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("qweRTY123$");
    request.setNewPassword("qwerty123ASD!");
    User mockUser = mock(User.class);

    doThrow(InvalidCurrentPasswordException.class).when(passwordService)
        .changePassword(mockUser, request);

    ResultActions response = mockMvc.perform(post("/api/patient/settings/password/change")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(user(mockUser)));

    response.andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("Password change failed"));
  }

  @Test
  public void changePassword_PasswordRepeats_ReturnsConflictRequest() throws Exception {
    ChangePasswordRequest request = new ChangePasswordRequest();
    request.setCurrentPassword("qweRTY123$");
    request.setNewPassword("qweRTY123$");
    User mockUser = mock(User.class);

    doThrow(PasswordDoesNotMeetRepetitionConstraint.class).when(passwordService)
        .changePassword(mockUser, request);

    ResultActions response = mockMvc.perform(post("/api/patient/settings/password/change")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request))
        .with(user(mockUser)));

    response.andExpect(status().isConflict())
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.message").value("Password change failed"));
  }
}
