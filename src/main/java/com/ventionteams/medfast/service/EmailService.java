package com.ventionteams.medfast.service;

import com.ventionteams.medfast.entity.User;

import jakarta.mail.MessagingException;
import java.io.IOException;


public interface EmailService {
    void sendVerificationEmail(User user) throws MessagingException, IOException;
}
