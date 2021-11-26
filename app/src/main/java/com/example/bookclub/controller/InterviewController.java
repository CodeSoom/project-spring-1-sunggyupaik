package com.example.bookclub.controller;

import com.example.bookclub.application.InterviewService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Interview;
import com.example.bookclub.security.UserAccount;
import com.example.bookclub.utils.PageUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/interviews")
public class InterviewController {
    private final InterviewService interviewService;
    private final PageUtil pageUtil;

    @Value("${countList}")
    private int countList;
    @Value("${countPage}")
    private int countPage;

    public InterviewController(InterviewService interviewService,
                               PageUtil pageUtil) {
        this.interviewService = interviewService;
        this.pageUtil = pageUtil;
    }

    @GetMapping
    public String interviewLists(@AuthenticationPrincipal UserAccount userAccount,
                                 @RequestParam(defaultValue = "1") int targetPage,
                                 Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        List<Interview> lists = interviewService.getInterviews(
                PageRequest.of(targetPage - 1, countList));
        model.addAttribute("lists", lists);
        int allListsCount = interviewService.getInterviewsAll().size();
        PageUtil pageUtils = pageUtil.makePage(allListsCount, targetPage, countList, countPage);
        model.addAttribute("pageUtils", pageUtils);
        userAccount.getAuthorities().forEach(auth -> {
           if(auth.toString().equals("ADMIN")) {
               model.addAttribute("adminAuthority", "true");
           }
        });

        return "interviews/interviews-list";
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
