package com.example.bookclub.controllers;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Study;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {
    private StudyService studyService;

    public HomeController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Study> lists = studyService.getStudies();

        model.addAttribute("study", lists);
        return "index";
    }
}
