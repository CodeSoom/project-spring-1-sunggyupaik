package com.example.bookclub.controllers;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/studys")
public class StudyController {
    private final StudyService studyService;

    public StudyController(StudyService studyService) {
        this.studyService = studyService;
    }

    @GetMapping("/{id}")
    public String studyDetail(@CurrentAccount Account account,
                              @PathVariable Long id, Model model) {
        Study study = studyService.getStudy(id);
        model.addAttribute("study", study);
        model.addAttribute("day", Day.getTitleFrom(study.getDay()));
        model.addAttribute("studyState", StudyState.getTitleFrom(study.getStudyState()));
        model.addAttribute("zone", Zone.getTitleFrom(study.getZone()));
        if(account == null) {
            return "studys/studys-detail";
        }

        if (account.getStudy() != null && account.getStudy().getId().equals(id)) {
            model.addAttribute("alreadyApplied", "true");
        } else {
            model.addAttribute("notApplied", "true");
        }

        return "studys/studys-detail";
    }

    @GetMapping("/save")
    public String studySave(@CurrentAccount Account account, Model model) {
        if (account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }
        model.addAttribute("day", Day.getAllDays());
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-save";
    }

    @GetMapping("/update/{id}")
    public String studyUpdate(@CurrentAccount Account account,
                              @PathVariable Long id, Model model) {
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }
        Study study = studyService.getStudy(id);
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        model.addAttribute("study", study);
        model.addAttribute("day", Day.getAllDays());
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-update";
    }

    @GetMapping
    public String studyList(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute("account", account);
        }
        List<Study> lists = studyService.getStudies();
        model.addAttribute("studys", lists);
        return "studys/studys-list";
    }
}
