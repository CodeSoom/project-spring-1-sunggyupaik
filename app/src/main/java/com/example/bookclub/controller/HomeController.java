package com.example.bookclub.controller;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.security.CurrentAccount;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final AccountService accountService;
    private final StudyService studyService;
    private final AccountAuthenticationService accountAuthenticationService;

    public HomeController(AccountService accountService,
                          StudyService studyService,
                          AccountAuthenticationService accountAuthenticationService) {
        this.accountService = accountService;
        this.studyService = studyService;
        this.accountAuthenticationService = accountAuthenticationService;
    }

    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model,
                       @AuthenticationPrincipal UserAccount userAccount) {
        if(account != null) {
            account = accountAuthenticationService.getAccountByEmail(account.getEmail());
            checkTopMenu(account, model);
        }

        long allAccountsCount = accountService.countAllAccounts();
        model.addAttribute("allAccountsCount", allAccountsCount);

        long allStudiesCount = studyService.countAllStudies();
        model.addAttribute("allStudiesCount", allStudiesCount);

        long allCloseStudiesCount = studyService.countCloseStudies(userAccount.getAccount().getId());
        model.addAttribute("allCloseStudiesCount", allCloseStudiesCount);

        long allEndStudiesCount = studyService.countEndStudies(userAccount.getAccount().getId());
        model.addAttribute("allEndStudiesCount", allEndStudiesCount);

        return "index";
    }

    private void checkTopMenu(Account account, Model model) {
        model.addAttribute("account", account);
        if (account.isMangerOf(account.getStudy())) {
            model.addAttribute("studyManager", account.getStudy());
        }
        if (account.isApplierOf(account.getStudy())) {
            model.addAttribute("studyApply", account.getStudy());
        }
    }
}
