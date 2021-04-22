package com.example.bookclub.controllers;

import com.example.bookclub.application.InterviewService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Interview;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }
    @GetMapping
    public String interviewLists(@CurrentAccount Account account,
                                 Model model) {
        if(account != null) {
            checkTopMenu(account, model);
        }
        List<Interview> lists = interviewService.getInterviews();
        model.addAttribute("interviews", lists);
        //List<Interview> list = interviewService.crawlAllInterviews();
        return "interviews/interviews-list";
    }

    private void checkTopMenu(@CurrentAccount Account account, Model model) {
        model.addAttribute("account", account);
        if (account.isMangerOf(account.getStudy())) {
            model.addAttribute("studyManager", account.getStudy());
        }
        if (account.isApplierOf(account.getStudy())) {
            model.addAttribute("studyApply", account.getStudy());
        }
    }
}
