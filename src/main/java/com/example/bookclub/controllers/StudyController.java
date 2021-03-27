package com.example.bookclub.controllers;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
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
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-save";
    }
}
