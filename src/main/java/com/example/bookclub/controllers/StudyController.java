package com.example.bookclub.controllers;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/studys")
public class StudyController {
    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/{id}")
    public String studyDetail(@PathVariable Long id, Model model) {
        Study study = studyService.getStudy(id);
        model.addAttribute("study", study);
        model.addAttribute("day", Day.getTitleFrom(study.getDay()));
        model.addAttribute("studyState", StudyState.getTitleFrom(study.getStudyState()));
        model.addAttribute("zone", Zone.getTitleFrom(study.getZone()));
        return "studys/studys-detail";
    }

    @GetMapping("/save")
    public String studySave(Model model) {
        model.addAttribute("day", Day.getAllDays());
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-save";
    }

    @GetMapping("/update/{id}")
    public String studyUpdate(@PathVariable Long id, Model model) {
        Study study = studyService.getStudy(id);
        model.addAttribute("study", study);
        model.addAttribute("day", Day.getAllDays());
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-update";
    }
}
