package com.example.bookclub.controllers;

import com.example.bookclub.application.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/save")
    public String usersSave() {
        return "users/users-save";
    }

    @GetMapping("/update")
    public String usersUpdate() {
        return "users/users-update";
    }
}
