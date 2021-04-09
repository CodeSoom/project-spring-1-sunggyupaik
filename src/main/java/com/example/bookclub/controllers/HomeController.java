package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final AccountService accountService;

    public HomeController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        long allAccountsCount = accountService.countAllAccounts();
        model.addAttribute("allAccountsCount", allAccountsCount);

        if(account != null) {
            model.addAttribute("account", account);
        }

        return "index";
    }
}
