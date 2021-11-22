package com.example.bookclub.controller.api;

import com.example.bookclub.application.EmailService;
import com.example.bookclub.dto.EmailRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailApiController {
    private final EmailService emailService;

    public EmailApiController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/authentication")
    public String sendAuthenticationNumber(@RequestBody EmailRequestDto emailRequestDto) {
        return emailService.sendAuthenticationNumber(emailRequestDto);
    }
}
