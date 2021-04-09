package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final AccountService accountService;
    private final StudyService studyService;

    public HomeController(AccountService accountService,
                          StudyService studyService) {
        this.accountService = accountService;
        this.studyService = studyService;
    }

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        long allAccountsCount = accountService.countAllAccounts();
        model.addAttribute("allAccountsCount", allAccountsCount);
        
        long allStudiesCount = studyService.countAllStudies();
        model.addAttribute("allStudiesCount", allStudiesCount);

        if(account != null) {
            model.addAttribute("account", account);
        }

        return "index";
    }
}
