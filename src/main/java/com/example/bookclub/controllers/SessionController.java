package com.example.bookclub.controllers;

import com.example.bookclub.application.AuthenticationService;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResultDto login(@RequestBody SessionCreateDto sessionCreateDto) {
        return authenticationService.createToken(sessionCreateDto);
    }
}
