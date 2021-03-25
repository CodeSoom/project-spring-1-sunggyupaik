package com.example.bookclub.controllers;

import com.example.bookclub.dto.EmailRequestDto;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/api/email")
public class EmailApiController {
    @PostMapping("/authentication")
    public String sendAuthenticationNumber(@RequestBody EmailRequestDto emailRequestDto) throws MessagingException {
        return "";
    }
}
