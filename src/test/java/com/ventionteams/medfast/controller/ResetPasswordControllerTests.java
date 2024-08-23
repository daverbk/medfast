package com.ventionteams.medfast.controller;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.dto.request.ResetPasswordRequest;
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
 * Tests the reset password controller functionality with integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(PostgreContainerExtension.class)
public class ResetPasswordControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private PasswordService resetPasswordService;

  @Test
  public void resetPassword_InvalidRequest_ReturnsBadRequest() throws Exception {
    ResetPasswordRequest resetPasswordRequest = new ResetPasswordRequest();
    resetPasswordRequest.setOtp("11111");

    ResultActions response = mockMvc.perform(post("/auth/password/reset")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(resetPasswordRequest)));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void resetPassword_ValidRequest_ResetsPasswordAndReturnsOk() throws Exception {
    ResetPasswordRequest request = new ResetPasswordRequest();
    request.setOtp("1111");
    request.setNewPassword("qwerty123ASD!");
    request.setEmail("johndoe@gmail.com");

    ResultActions response = mockMvc.perform(post("/auth/password/reset")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)));

    verify(resetPasswordService, times(1)).resetPassword(request);
    response.andExpect(status().isOk());
  }
}
