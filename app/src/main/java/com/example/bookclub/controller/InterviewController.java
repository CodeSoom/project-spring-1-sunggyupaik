package com.example.bookclub.controller;

import com.example.bookclub.application.interview.InterviewService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.InterviewDto;
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

import static org.apache.commons.lang3.ObjectUtils.isEmpty;

/**
 * 인터뷰 조회 페이지를 요청한다
 */
@Controller
@RequestMapping("/interviews")
public class InterviewController {
    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    /**
     * 인터뷰 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @param pageable 페이징 정보
     * @param search 검색어
     * @return 인터뷰 조회 페이지
     */
    @GetMapping
    public String interviewLists(@AuthenticationPrincipal UserAccount userAccount, Model model,
                                 @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable,
                                 @RequestParam(defaultValue = "") String search) {
        checkTopMenu(userAccount.getAccount(), model);

        Page<InterviewDto.InterviewResultDto> page = null;
        if(isEmpty(search)) {
            page = interviewService.getAllInterviews(pageable);
        } else {
            page = interviewService.getInterviews(search, pageable);
        }

        model.addAttribute("list", page.getContent());
        model.addAttribute("page", PageResultDto.of(page));

        userAccount.getAuthorities().forEach(auth -> {
           if(auth.toString().equals("ADMIN")) {
               model.addAttribute("adminAuthority", "true");
           }
        });

        return "interviews/interviews-list";
    }

    /**
     * 로그인한 사용자의 스터디 개설, 참여 여부를 확인한다
     *
     * @param account 로그인한 사용자
     * @param model 모델
     */
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
