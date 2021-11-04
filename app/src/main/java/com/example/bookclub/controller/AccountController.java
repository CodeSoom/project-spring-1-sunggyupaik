package com.example.bookclub.controller;

import com.example.bookclub.domain.Account;
import com.example.bookclub.security.AccountAuthenticationService;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class AccountController {
    private final AccountAuthenticationService accountAuthenticationService;

    public AccountController(AccountAuthenticationService accountAuthenticationService) {
        this.accountAuthenticationService = accountAuthenticationService;
    }

    @GetMapping("/save")
    public String usersSave(@CurrentAccount Account account) {
        if(account != null) {
            return "redirect:/";
        }
        return "users/users-save";
    }

    @PreAuthorize("#account.id == #id")
    @GetMapping("/update/{id}")
    public String usersUpdate(@CurrentAccount Account account,
                              @PathVariable Long id, Model model) {
        account = accountAuthenticationService.getAccountByEmail(account.getEmail());
        model.addAttribute("account", account);
        checkTopMenu(account, model);
        return "users/users-update";
    }

    @PreAuthorize("#account.id == #id")
    @GetMapping("/update/password/{id}")
    public String usersPasswordUpdate(@CurrentAccount Account account,
                                      @PathVariable Long id, Model model) {
        model.addAttribute("account", account);
        checkTopMenu(account, model);
        return "users/users-update-password";
    }

    private void checkTopMenu(Account account, Model model) {
        if (account.isMangerOf(account.getStudy()))
            model.addAttribute("studyManager", account.getStudy());

        if (account.isApplierOf(account.getStudy()))
            model.addAttribute("studyApply", account.getStudy());
    }
}
