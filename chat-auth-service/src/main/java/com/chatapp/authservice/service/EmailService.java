package com.chatapp.authservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public void sendOtpEmail(String to, String otp) {
        // For development, we'll just log the OTP
        // In a production environment, you would use JavaMailSender to send the email
        logger.info("Sending OTP to: {}", to);
        logger.info("OTP: {}", otp);
    }
}
