package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.study.StudyService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 루트 페이지를 요청한다
 */
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

    /**
     * 루트 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @return 루트 페이지
     */
    @GetMapping("/")
    public String home(@CurrentAccount Account account, Model model) {
        if(account != null) {
            Account savedAccount = accountAuthenticationService.getAccountByEmail(account.getEmail());
            checkTopMenu(savedAccount, model);
        }

        long allAccountsCount = accountService.getAllAccountsCount();
        model.addAttribute("allAccountsCount", allAccountsCount);

        long allStudiesCount = studyService.getAllStudiesCount();
        model.addAttribute("allStudiesCount", allStudiesCount);

        long allCloseStudiesCount = studyService.getStudiesCount(StudyState.CLOSE);
        model.addAttribute("allCloseStudiesCount", allCloseStudiesCount);

        long allEndStudiesCount = studyService.getStudiesCount(StudyState.END);
        model.addAttribute("allEndStudiesCount", allEndStudiesCount);

        return "index";
    }

    /**
     * 로그인한 사용자의 스터디 개설, 참여 여부를 확인한다
     *
     * @param account 로그인한 사용자
     * @param model 모델
     */
    private void checkTopMenu(Account account, Model model) {
        if (account.isMangerOf(account.getStudy()))
            model.addAttribute("studyManager", account.getStudy());

        if (account.isApplierOf(account.getStudy()))
            model.addAttribute("studyApply", account.getStudy());

        model.addAttribute("account", account);
    }
}
