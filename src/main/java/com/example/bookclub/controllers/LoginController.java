package com.example.bookclub.controllers;

import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.security.AccountAuthenticationService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final AccountAuthenticationService accountAuthenticationService;

    public LoginController(AccountAuthenticationService accountAuthenticationService) {
        this.accountAuthenticationService = accountAuthenticationService;
    }
    @GetMapping
    public String login() {
        return "login";
    }

    @PostMapping("/signup")
    public String signup(@RequestBody SessionCreateDto sessionCreateDto) {
        accountAuthenticationService.login(sessionCreateDto);
        return "redirect:/";
    }
}
