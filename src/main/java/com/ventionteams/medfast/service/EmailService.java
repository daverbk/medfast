package com.ventionteams.medfast.service;

import com.ventionteams.medfast.config.properties.SpringConfig;
import com.ventionteams.medfast.entity.User;
import com.ventionteams.medfast.service.auth.VerificationUrlService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender emailSender;
    private final TemplateEngine templateEngine;
    private final VerificationUrlService verificationUrlService;
    private final SpringConfig springConfig;

    public void sendVerificationEmail(User user) throws MessagingException, IOException {
        Context context = new Context();
        context.setVariable("userName", user.getName());
        context.setVariable("verificationLink", verificationUrlService.generateVerificationUrl(user.getEmail()));
        context.setVariable("medfastMailbox", springConfig.getMail().getUsername());

        String content = templateEngine.process("verification_email", context);

        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);

        helper.setFrom(springConfig.getMail().getUsername());
        helper.setTo(user.getEmail());
        helper.setSubject("Complete Your Registration on Medfast");
        helper.setText(content, true);

        emailSender.send(message);
    }
}
