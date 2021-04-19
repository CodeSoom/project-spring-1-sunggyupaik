package com.example.bookclub.controllers;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.errors.StudyAlreadyStartedException;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
        if(account != null) {
            checkTopMenu(account, model);
        }

        Study study = studyService.getStudy(id);
        model.addAttribute("study", study);
        model.addAttribute("day", Day.getTitleFrom(study.getDay()));
        model.addAttribute("studyState", StudyState.getTitleFrom(study.getStudyState()));
        model.addAttribute("zone", Zone.getTitleFrom(study.getZone()));
        if(account == null) {
            return "studys/studys-detail";
        }

        if(study.isAlreadyStarted() || study.isNotOpened()) {
            return "studys/studys-detail";
        }
        if(account.isMangerOf(study)) {
            model.addAttribute("studyAdmin", "true");
        }
        if(account.isApplierOf(study)) {
            model.addAttribute("studyApplier", "true");
        }

        return "studys/studys-detail";
    }

    @GetMapping("/save")
    public String studySave(@CurrentAccount Account account,
                            @RequestParam String bookName,
                            @RequestParam String bookImage,
                            Model model) {
        if (account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        } else {
            checkTopMenu(account, model);
        }

        model.addAttribute("bookName", bookName);
        model.addAttribute("bookImage", bookImage);
        model.addAttribute("day", Day.getAllDays());
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-save";
    }

    @GetMapping("/update/{id}")
    public String studyUpdate(@CurrentAccount Account account,
                              @PathVariable Long id, Model model) {
        Study study = studyService.getStudy(id);
        if(study.isAlreadyStarted()) {
            throw new StudyAlreadyStartedException();
        }
        
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        } else {
            checkTopMenu(account, model);
        }
        
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        model.addAttribute("study", study);
        model.addAttribute("day", Day.getAllDays());
        model.addAttribute("studyState", StudyState.getAllStudyStates());
        model.addAttribute("zone", Zone.getAllZones());
        return "studys/studys-update";
    }

    @GetMapping("/open")
    public String studyOpenList(@CurrentAccount Account account, Model model) {
        if(account != null) {
            checkTopMenu(account, model);
        }

        List<Study> lists = studyService.getStudiesByStudyState(StudyState.OPEN);
        model.addAttribute("studys", lists);
        model.addAttribute("studyState", StudyState.getTitleFrom(StudyState.OPEN));
        return "studys/studys-list";
    }

    @GetMapping("/close")
    public String studyCloseList(@CurrentAccount Account account, Model model) {
        if(account != null) {
            checkTopMenu(account, model);
        }

        List<Study> lists = studyService.getStudiesByStudyState(StudyState.CLOSE);
        model.addAttribute("studys", lists);
        model.addAttribute("studyState", StudyState.getTitleFrom(StudyState.CLOSE));
        return "studys/studys-list";
    }

    @GetMapping("/end")
    public String studyEndList(@CurrentAccount Account account, Model model) {
        if(account != null) {
            checkTopMenu(account, model);
        }

        List<Study> lists = studyService.getStudiesByStudyState(StudyState.END);
        model.addAttribute("studys", lists);
        model.addAttribute("studyState", StudyState.getTitleFrom(StudyState.END));
        return "studys/studys-list";
    }

    @GetMapping("/{id}/users")
    public String studyApplyUserList(@CurrentAccount Account account,
                                     @PathVariable Long id,
                                     Model model) {
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        } else {
            checkTopMenu(account, model);
        }

        model.addAttribute("study", account.getStudy());
        List<Account> accounts = studyService.getStudy(id).getAccounts();
        model.addAttribute("accounts", accounts);
        return "studys/studys-users-list";
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
