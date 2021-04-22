package com.example.bookclub.controllers;

import com.example.bookclub.application.InterviewService;
import com.example.bookclub.domain.Interview;
import org.springframework.stereotype.Controller;
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
    public String interviewLists() {
        List<Interview> list = interviewService.crawlAllInterviews();
        return "interviews/interviews-list";
    }
}
