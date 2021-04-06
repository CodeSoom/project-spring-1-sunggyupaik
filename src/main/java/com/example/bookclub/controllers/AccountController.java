package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.domain.Account;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class AccountController {
    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/save")
    public String usersSave() {
        return "users/users-save";
    }

    @GetMapping("/update/{id}")
    public String usersUpdate(@PathVariable Long id, Model model) {
        Account account = accountService.getUser(id);
        model.addAttribute("user", account);
        return "users/users-update";
    }
}
