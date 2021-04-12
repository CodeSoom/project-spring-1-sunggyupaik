package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
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

@RestController
@RequestMapping("/api/users")
public class AccountApiController {
    private final AccountService accountService;

    public AccountApiController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public AccountResultDto detail(@PathVariable Long id) {
        Account account = accountService.getUser(id);
        return AccountResultDto.of(account);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResultDto create(AccountCreateDto accountCreateDto) {
        AccountResultDto account = accountService.createUser(accountCreateDto);

        return account;
    }

    @PatchMapping("/{id}")
    public AccountResultDto update(@PathVariable Long id, @RequestBody AccountUpdateDto accountUpdateDto) {
        return accountService.updateUser(id, accountUpdateDto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public AccountResultDto delete(@PathVariable Long id) {
        return accountService.deleteUser(id);
    }
}
