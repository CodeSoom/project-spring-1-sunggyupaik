package com.example.bookclub.application;

import com.example.bookclub.dto.EmailRequestDto;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendAuthenticationNumber(EmailRequestDto emailRequestDto) {
        MimeMessage message = createMessage(emailRequestDto.getEmail());

        try {
            javaMailSender.send(message);
        } catch(MailException ex) {
            throw new MailIllegalArgumentException();
        }
    }
}
