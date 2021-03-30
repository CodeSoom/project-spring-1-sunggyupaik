package com.example.bookclub.controllers;

import com.example.bookclub.dto.SessionCreateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String login(@RequestBody SessionCreateDto sessionCreateDto) {
        return "{\"accessToken\":\"12345678\"}";
    }
}
