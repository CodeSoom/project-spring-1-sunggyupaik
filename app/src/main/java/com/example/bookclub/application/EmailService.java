package com.example.bookclub.application;

import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.dto.EmailDto;
import com.example.bookclub.errors.EmailBadRequestException;
import com.example.bookclub.errors.MessageCreateBadRequestException;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

/**
 * 인증번호 전송, 메세지 생성, 인증번호 생성을 한다.
 */
@Service
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final EmailAuthenticationRepository emailAuthenticationRepository;

    public EmailService(JavaMailSender javaMailSender,
                        EmailAuthenticationRepository emailAuthenticationRepository
    ) {
        this.javaMailSender = javaMailSender;
        this.emailAuthenticationRepository = emailAuthenticationRepository;
    }

    /**
     * 주어진 이메일 정보로 인증번호를 전송하고 이메일을 반환한다.
     *
     * @param emailRequestDto 이메일 정보
     * @return 인증번호를 전송한 이메일
     * @throws EmailBadRequestException 메일 전송 요청이 잘못된 경우
     */
    public EmailDto.EmailSendResultDto sendAuthenticationNumber(EmailDto.EmailRequestDto emailRequestDto) {
        String authenticationNumber = createAuthenticationNumber();
        String email = emailRequestDto.getEmail();
        MimeMessage message = null;
        message = createMessage(email, authenticationNumber);

        try {
            javaMailSender.send(message);
        } catch(MailException ex) {
            System.out.println(ex.getMessage());
            throw new EmailBadRequestException(email);
        }

        emailAuthenticationRepository.save(
                EmailAuthentication.builder()
                        .email(email)
                        .authenticationNumber(authenticationNumber)
                        .build()
        );

        return EmailDto.EmailSendResultDto.of(email, authenticationNumber);
    }

    /**
     * 주어진 사용자 이메일과 인증번호로 전송할 메세지를 생성하고 반환한다.
     *
     * @param email 사용자 이메일
     * @param authenticationNumber 인증번호
     * @return 전송할 메세지
     * @throws MessageCreateBadRequestException 전송할 메세지 만들기가 안되는 경우
     */
    private MimeMessage createMessage(String email, String authenticationNumber) {
        MimeMessage  message = javaMailSender.createMimeMessage();

        try {
            message.addRecipients(Message.RecipientType.TO, email);
            message.setSubject("BookClub 인증번호");
            message.setText(authenticationNumber);
            System.out.println("BookClub 인증번호=" + authenticationNumber);
        } catch(MessagingException ex) {
            System.out.println(ex.getMessage());
            throw new MessageCreateBadRequestException();
        }

        return message;
    }

    public EmailDto.EmailSendResultDto saveAuthenticationNumber(EmailDto.EmailRequestDto emailRequestDto) {
        String authenticationNumber = createAuthenticationNumber();
        String email = emailRequestDto.getEmail();
        System.out.println(authenticationNumber+"=authenticationNumber");

        emailAuthenticationRepository.save(
                EmailAuthentication.builder()
                        .email(email)
                        .authenticationNumber(authenticationNumber)
                        .build()
        );

        return EmailDto.EmailSendResultDto.of(email, authenticationNumber);
    }

    /**
     * 인증번호를 생성하고 반환한다.
     *
     * @return 생성된 인증번호
     */
    public String createAuthenticationNumber() {
        double dValue = Math.random();
        int iValue = (int)(dValue * 100000);
        return Integer.toString(iValue);
    }
}
