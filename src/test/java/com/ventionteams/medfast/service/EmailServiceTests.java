package com.ventionteams.medfast.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.config.properties.SpringConfig.Mail;
import com.ventionteams.medfast.entity.Person;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.auth.VerificationUrlService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Checks email service functionality with unit tests.
 */
@ExtendWith(MockitoExtension.class)
public class EmailServiceTests {

  @Mock
  private JavaMailSender emailSender;

  @Mock
  private VerificationUrlService verificationUrlService;

  @Mock
  private SpringConfig springConfig;

  @Mock
  private TemplateEngine templateEngine;

  @InjectMocks
  private EmailService emailService;

  @Mock
  private MimeMessage mimeMessage;

  @Test
  public void sendVerificationEmail_EmptyUser_ExceptionThrown() {
    Assertions.assertThrows(NullPointerException.class,
        () -> emailService.sendVerificationEmail(null));
  }

  @Test
  public void sendVerificationEmail_CorrectInput_SendsEmail() throws MessagingException {
    User user = mock(User.class);
    Person person = mock(Person.class);
    Mail mail = mock(Mail.class);
    String expectedContent = "<html>Verification content</html>";

    when(user.getPerson()).thenReturn(person);
    when(person.getName()).thenReturn("John Doe");
    when(user.getEmail()).thenReturn("user@example.com");
    when(verificationUrlService.generateVerificationUrl(user.getEmail()))
        .thenReturn("http://example.com/verify?token=12345");
    when(springConfig.mail()).thenReturn(mail);
    when(mail.username()).thenReturn("support@example.com");
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn(
        expectedContent);
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    doNothing().when(emailSender).send(any(MimeMessage.class));

    emailService.sendVerificationEmail(user);

    verify(emailSender, times(1)).createMimeMessage();
    verify(emailSender, times(1)).send(mimeMessage);
  }

  @Test
  public void sendResetPasswordEmail_CorrectInput_SendsEmail() throws MessagingException {
    User user = mock(User.class);
    Mail mail = mock(Mail.class);
    String expectedContent = "<html>Verification content</html>";

    when(springConfig.mail()).thenReturn(mail);
    when(mail.username()).thenReturn("support@example.com");
    when(templateEngine.process(anyString(), any(Context.class))).thenReturn(
        expectedContent);
    when(user.getEmail()).thenReturn("user@example.com");
    when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    doNothing().when(emailSender).send(any(MimeMessage.class));

    emailService.sendResetPasswordEmail(user, "token");

    verify(emailSender, times(1)).createMimeMessage();
    verify(emailSender, times(1)).send(mimeMessage);
  }
}
