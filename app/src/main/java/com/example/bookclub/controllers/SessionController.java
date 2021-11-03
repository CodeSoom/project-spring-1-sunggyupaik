package com.example.bookclub.controllers;

import com.example.bookclub.application.AuthenticationService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/session")
public class SessionController {
    private final AuthenticationService authenticationService;

    public SessionController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

//    @PostMapping
//    @ResponseStatus(HttpStatus.CREATED)
//    public SessionResultDto login(@RequestBody SessionCreateDto sessionCreateDto) {
//        return authenticationService.createToken(sessionCreateDto);
//    }
}
