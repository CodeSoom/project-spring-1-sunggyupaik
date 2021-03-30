package com.example.bookclub.controllers;

import com.example.bookclub.application.UserService;
import com.example.bookclub.domain.User;
import com.example.bookclub.dto.UserCreateDto;
import com.example.bookclub.dto.UserResultDto;
import com.example.bookclub.dto.UserUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserApiController {
    private final UserService userService;

    public UserApiController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public UserResultDto detail(@PathVariable Long id) {
        User user = userService.getUser(id);
        return UserResultDto.of(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResultDto create(@RequestBody @Valid UserCreateDto userCreateDto) {
        return userService.createUser(userCreateDto);
    }

    @PatchMapping("/{id}")
    public UserResultDto update(@PathVariable Long id, @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(id, userUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public UserResultDto delete(@PathVariable Long id) {
        return userService.deleteUser(id);
    }
}
