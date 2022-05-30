package com.example.bookclub.controller.api;

import com.example.bookclub.application.EmailService;
import com.example.bookclub.common.CommonResponse;
import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.dto.EmailSendResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
     * @return 인증번호를 전송한 이메일
     */
    @PostMapping("/authentication")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<EmailSendResultDto> sendAuthenticationNumber(
            @RequestBody EmailRequestDto emailRequestDto
    ) {
        EmailSendResultDto response = emailService.sendAuthenticationNumber(emailRequestDto);
        return CommonResponse.success(response);
    }
}
