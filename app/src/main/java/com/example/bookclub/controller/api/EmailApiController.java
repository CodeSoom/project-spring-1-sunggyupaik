package com.example.bookclub.controller.api;

import com.example.bookclub.application.EmailService;
import com.example.bookclub.dto.EmailRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 이메일 인증번호 전송을 요청한다
 */
@RestController
@RequestMapping("/api/email")
public class EmailApiController {
    private final EmailService emailService;

    public EmailApiController(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 주어진 이메일 정보로 인증번호를 전송한다
     *
     * @param emailRequestDto 발송할 이메일
     * @return 인증번호
     */
    @PostMapping("/authentication")
    public String sendAuthenticationNumber(@RequestBody EmailRequestDto emailRequestDto) {
        return emailService.sendAuthenticationNumber(emailRequestDto);
    }
}
