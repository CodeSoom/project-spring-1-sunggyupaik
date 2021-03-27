package com.example.bookclub.controllers;

import com.example.bookclub.domain.Day;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/studys")
public class StudyController {
    @GetMapping("/save")
    public String studySave(Model model) {
        model.addAttribute("day", Day.getAllDays());
        return "studys/studys-save";
    }
}
