package com.ventionteams.medfast.service;

import com.ventionteams.medfast.entity.User;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class EmailServiceNoOp implements EmailService {
    @Override
    public void sendVerificationEmail(User user) throws MessagingException, IOException {

    }
}
