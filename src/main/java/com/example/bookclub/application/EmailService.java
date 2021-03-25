package com.example.bookclub.application;

import com.example.bookclub.dto.EmailRequestDto;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendAuthenticationNumber(EmailRequestDto emailRequestDto) {
        return "";
    }
}
