package com.example.bookclub.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/email")
public class EmailApiController {
    @PostMapping("/authentication")
    public String sendAuthenticationNumber(@RequestBody String to) {
        return "";
    }
}
