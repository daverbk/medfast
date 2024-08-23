package com.ventionteams.medfast.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import com.ventionteams.medfast.exception.auth.TokenExpiredException;
import com.ventionteams.medfast.exception.auth.TokenNotFoundException;
import com.ventionteams.medfast.service.password.OneTimePasswordService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/**
 * Tests the one time password controller functionality with integration tests.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(PostgreContainerExtension.class)
public class OneTimePasswordControllerTests {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private OneTimePasswordService oneTimePasswordService;

  @Test
  public void sendOtp_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
    String email = "invalid";

    ResultActions response = mockMvc.perform(post("/auth/otp")
        .param("email", email));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void sendOtp_InvalidEmail_ReturnsBadRequest() throws Exception {
    String email = "invalid@example.com";

    doThrow(new UsernameNotFoundException("User is not found")).when(oneTimePasswordService)
        .sendResetPasswordEmail(email);

    ResultActions response = mockMvc.perform(post("/auth/otp")
        .param("email", email));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void sendOtp_MessagingExceptionOccurs_ReturnsInternalServerError() throws Exception {
    String email = "test@example.com";

    doThrow(new MessagingException("Failed to send email")).when(
        oneTimePasswordService).sendResetPasswordEmail(email);

    ResultActions response = mockMvc.perform(post("/auth/otp")
        .param("email", email));

    response.andExpect(status().isInternalServerError());
  }

  @Test
  public void sendOtp_ValidEmail_SendsOtpAndReturnsOk() throws Exception {
    String email = "test@exampl.com";

    doNothing().when(oneTimePasswordService).sendResetPasswordEmail(email);

    ResultActions response = mockMvc.perform(post("/auth/otp")
        .param("email", email));

    verify(oneTimePasswordService, times(1)).sendResetPasswordEmail(email);
    response.andExpect(status().isOk());
  }

  @Test
  public void verifyOtp_InvalidEmailFormat_ReturnsBadRequest() throws Exception {
    String email = "invalid";
    String token = "1111";

    ResultActions response = mockMvc.perform(post("/auth/otp/verify")
        .param("email", email)
        .param("token", token));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void verifyOtp_InvalidTypeFormat_ReturnsBadRequest() throws Exception {
    String email = "test@exampl.com";
    String token = "111111";

    ResultActions response = mockMvc.perform(post("/auth/otp/verify")
        .param("email", email)
        .param("token", token));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void verifyOtp_InvalidEmailOrToken_ReturnsNotFound() throws Exception {
    String email = "test@example.com";
    String token = "1111";

    doThrow(new TokenNotFoundException(email,
        "One time password token is not found for user")).when(oneTimePasswordService)
        .verify(email, token);

    ResultActions response = mockMvc.perform(post("/auth/otp/verify")
        .param("email", email)
        .param("token", token));

    response.andExpect(status().isNotFound());
  }

  @Test
  public void verifyOtp_TokenExpired_ReturnsBadRequest() throws Exception {
    String email = "test@example.com";
    String token = "1111";

    doThrow(new TokenExpiredException(token, "One time password token is expired"))
        .when(oneTimePasswordService)
        .verify(email, token);

    ResultActions response = mockMvc.perform(post("/auth/otp/verify")
        .param("email", email)
        .param("token", token));

    response.andExpect(status().isBadRequest());
  }

  @Test
  public void verifyOtp_ValidEmailAndToken_VerifiesOtpAndReturnsOk() throws Exception {
    String email = "test@example.com";
    String token = "1111";

    doNothing().when(oneTimePasswordService).verify(email, token);

    ResultActions response = mockMvc.perform(post("/auth/otp/verify")
        .param("email", email)
        .param("token", token));

    verify(oneTimePasswordService, times(1)).verify(email, token);
    response.andExpect(status().isOk());
  }
}
