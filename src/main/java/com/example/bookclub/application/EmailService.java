package com.example.bookclub.application;

import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.errors.MailIllegalArgumentException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public String sendAuthenticationNumber(EmailRequestDto emailRequestDto) throws MessagingException {
        MimeMessage message = createMessage(emailRequestDto.getEmail());

        try {
            javaMailSender.send(message);
        } catch(MailException ex) {
            throw new MailIllegalArgumentException();
        }

        return "message";
    }

    private MimeMessage createMessage(String to) throws MessagingException {
        MimeMessage  message = javaMailSender.createMimeMessage();

        String code = createAuthenticationNumber();
        message.addRecipients(Message.RecipientType.TO, to);
        message.setSubject("BookClub 인증번호");
        message.setText(code);

        return message;
    }

    public String createAuthenticationNumber() {
        double dValue = Math.random();
        int iValue = (int)(dValue * 100000);
        return Integer.toString(iValue);
    }
}
