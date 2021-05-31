package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.dto.SessionCreateDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final AccountService accountService;

    public LoginController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String login() {
        return "login";
    }

    @PostMapping("/signup")
    @ResponseBody
    public String signup(@RequestBody SessionCreateDto sessionCreateDto) {
        accountService.signup(sessionCreateDto);
        return "true";
    }
}
