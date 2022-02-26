package com.example.bookclub.controller;

import com.example.bookclub.application.InterviewService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.InterviewResultDto;
import com.example.bookclub.dto.PageResultDto;
import com.example.bookclub.security.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public String interviewLists(@AuthenticationPrincipal UserAccount userAccount, Model model,
                                 @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable,
                                 @RequestParam(defaultValue = "") String search) {
        checkTopMenu(userAccount.getAccount(), model);

        Page<InterviewResultDto> page = interviewService.getInterviews(search, pageable);
        model.addAttribute("list", page.getContent());
        model.addAttribute("page", PageResultDto.of(page));

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
