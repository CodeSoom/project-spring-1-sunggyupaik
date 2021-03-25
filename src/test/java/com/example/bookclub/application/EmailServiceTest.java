package com.example.bookclub.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;

class EmailServiceTest {
    private static final String EXISTED_EMAIL = "melon9751@naver.com";

    private EmailService emailService;

    @Autowired
    private JavaMailSender javaMailSender;

    @BeforeEach
    void setUp() {
    }

    @Test
    void sendAuthenticationNumber() {
    }
}
