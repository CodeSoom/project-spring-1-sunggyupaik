package com.example.bookclub.application;

import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.errors.EmailBadRequestException;
import com.example.bookclub.errors.MessageCreateBadRequestException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final String code;

    public EmailService(JavaMailSender javaMailSender,
                        EmailAuthenticationRepository emailAuthenticationRepository
    ) {
        this.javaMailSender = javaMailSender;
        this.emailAuthenticationRepository = emailAuthenticationRepository;
        this.code = createAuthenticationNumber();
    }

    public String sendAuthenticationNumber(EmailRequestDto emailRequestDto) {
        String email = emailRequestDto.getEmail();
        MimeMessage message = null;
        message = createMessage(email);

        try {
            javaMailSender.send(message);
        } catch(MailException ex) {
            System.out.println(ex.getMessage());
            throw new EmailBadRequestException(email);
        }

        emailAuthenticationRepository.save(new EmailAuthentication(email, code));

        return email;
    }

    private MimeMessage createMessage(String to) {
        MimeMessage  message = javaMailSender.createMimeMessage();

        try {
            message.addRecipients(Message.RecipientType.TO, to);
            message.setSubject("BookClub 인증번호");
            message.setText(code);
            System.out.println("BookClub 인증번호=" + code);
        } catch(MessagingException ex) {
            System.out.println(ex.getMessage());
            throw new MessageCreateBadRequestException();
        }

        return message;
    }

    public static String createAuthenticationNumber() {
        double dValue = Math.random();
        int iValue = (int)(dValue * 100000);
        return Integer.toString(iValue);
    }
}
