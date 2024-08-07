package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.auth.VerificationUrlService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

/**
 * Email service responsible for sending emails to users.
 */
@Service
@RequiredArgsConstructor
public class EmailService {

  private static final String LOGO_PATH = "templates/logos/logo.png";
  private static final String WATERMARK_PATH = "templates/logos/watermark.png";

  private static final String VERIFICATION_EMAIL_SUBJECT = "Medfast: Complete Your Registration";
  private static final String RESET_PASSWORD_EMAIL_SUBJECT = "Medfast: Reset Your Password";

  private static final String VERIFICATION_EMAIL_TEMPLATE = "verification";
  private static final String RESET_PASSWORD_EMAIL_TEMPLATE = "password_reset";

  private final JavaMailSender emailSender;
  private final TemplateEngine templateEngine;
  private final VerificationUrlService verificationUrlService;
  private final SpringConfig springConfig;

  /**
   * Send a verification email to the user with a verification link.
   */
  public void sendVerificationEmail(User user) throws MessagingException {
    Context context = new Context();
    context.setVariable("userName", user.getPerson().getName());
    context.setVariable("verificationLink",
        verificationUrlService.generateVerificationUrl(user.getEmail()));
    context.setVariable("supportMailbox", springConfig.mail().username());
    String content = templateEngine.process(VERIFICATION_EMAIL_TEMPLATE, context);
    sendMimeMessage(VERIFICATION_EMAIL_SUBJECT, user.getEmail(), content);
  }

  /**
   * Send a reset password email to the user with a one time password.
   */
  public void sendResetPasswordEmail(User user, String token) throws MessagingException {
    Context context = new Context();
    context.setVariable("token", token);
    context.setVariable("supportMailbox", springConfig.mail().username());
    String content = templateEngine.process(RESET_PASSWORD_EMAIL_TEMPLATE, context);
    sendMimeMessage(RESET_PASSWORD_EMAIL_SUBJECT, user.getEmail(), content);
  }

  private void sendMimeMessage(String subject, String recipient, String content)
      throws MessagingException {
    MimeMessage message = emailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

    helper.setSubject(subject);
    helper.setFrom(springConfig.mail().username());
    helper.setTo(recipient);
    helper.setText(content, true);
    helper.addInline("logo", new ClassPathResource(LOGO_PATH));
    helper.addInline("watermark", new ClassPathResource(WATERMARK_PATH));

    emailSender.send(message);
  }
}
