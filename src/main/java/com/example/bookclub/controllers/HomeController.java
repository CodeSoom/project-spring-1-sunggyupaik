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

        long allOpenStudiesCount = studyService.countOpenStudies();
        model.addAttribute("allOpenStudiesCount", allOpenStudiesCount);

        long allEndStudiesCount = studyService.countEndStudies();
        model.addAttribute("allEndStudiesCount", allEndStudiesCount);
        if (account == null) {
            return "index";
        }

        model.addAttribute("account", account);
        if (account.getStudy() != null &&
                account.getStudy().getEmail().equals(account.getEmail())) {
            model.addAttribute("admin", account.getStudy());
        }

        return "index";
    }
}
