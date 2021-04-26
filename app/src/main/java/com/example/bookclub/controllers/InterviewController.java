package com.example.bookclub.controllers;

import com.example.bookclub.application.InterviewService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Interview;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/interviews")
public class InterviewController {
    private final InterviewService interviewService;
    @Value("${countList}")
    private int countList;

    @Value("${countPage}")
    private int countPage;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @GetMapping
    public String interviewLists(@CurrentAccount Account account,
                                 @RequestParam(defaultValue = "1") String targetPage,
                                 Model model) {
        if(account != null) {
            checkTopMenu(account, model);
        }
        makePageable(targetPage, model);
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

    private void makePageable(String targetPage, Model model) {
        int allInterviewsCount = interviewService.getInterviewsAll().size();
        int totalPage = allInterviewsCount / countList;
        if(allInterviewsCount % countList > 0) {
            totalPage += 1;
        }

        int parseTargetPage = Integer.parseInt(targetPage) - 1;
        if(parseTargetPage + 1 > totalPage) {
            parseTargetPage = totalPage - 1;
        }

        Pageable pageable = PageRequest.of(parseTargetPage, countList);
        List<Interview> lists = interviewService.getInterviews(pageable);

        int nowPage = Integer.parseInt(targetPage);
        if(nowPage > totalPage) {
            nowPage = totalPage;
        }

        int firstPage = ((nowPage - 1) / countPage) * countPage + 1;
        int lastPage = firstPage + countPage - 1;
        if(lastPage > totalPage) {
            lastPage = totalPage;
        }

        int previous = firstPage -1;
        if(previous < 0) previous = 1;
        int next = lastPage + 1;
        List<Integer> pageNumberList = new ArrayList<>();
        for(int i=firstPage; i<=lastPage; i++) {
            pageNumberList.add(i);
        }

        model.addAttribute("interviews", lists);
        model.addAttribute("previous", previous);
        model.addAttribute("targetPage", targetPage);
        model.addAttribute("pageNumberList", pageNumberList);
        model.addAttribute("next", next);
        model.addAttribute("totalPage", totalPage);
    }
}
