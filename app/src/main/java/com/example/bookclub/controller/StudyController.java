package com.example.bookclub.controller;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.PageResultDto;
import com.example.bookclub.dto.StudyCreateInfoDto;
import com.example.bookclub.dto.StudyDetailResultDto;
import com.example.bookclub.dto.StudyInfoResultDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.security.CurrentAccount;
import com.example.bookclub.security.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/studys")
public class StudyController {
    private final StudyService studyService;
    private final AccountService accountService;

    public StudyController(StudyService studyService,
                           AccountService accountService) {
        this.studyService = studyService;
        this.accountService = accountService;
    }

    @GetMapping("/{id}")
    public String studyDetail(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        StudyDetailResultDto detailedStudy = studyService.getDetailedStudy(userAccount, id);
        model.addAttribute("detailedStudy", detailedStudy);

        Study study = studyService.getStudy(id);
        if (study.isAlreadyStarted() || study.isNotOpened()) {
            return "studys/studys-detail";
        }
        if (userAccount.getAccount().isMangerOf(study)) {
            model.addAttribute("studyAdmin", "true");
        }
        if (userAccount.getAccount().isApplierOf(study)) {
            model.addAttribute("studyApplier", "true");
        }

        return "studys/studys-detail";
    }

    @GetMapping("/save")
    public String studySave(@CurrentAccount Account account,
                            @RequestParam(defaultValue = "") String bookName,
                            @RequestParam(defaultValue = "") String bookImage,
                            Model model) {
        checkTopMenu(account, model);

        StudyCreateInfoDto studyCreateInfoDto = StudyCreateInfoDto.of(
                bookName, bookImage, Day.getAllDays(), StudyState.getAllStudyStates(), Zone.getAllZones()
        );

        model.addAttribute("StudyCreateInfoDto", studyCreateInfoDto);

        return "studys/studys-save";
    }

    @PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @GetMapping("/update/{id}")
    public String studyUpdate(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        Study study = studyService.getStudy(id);

        checkTopMenu(userAccount.getAccount(), model);

        model.addAttribute("study", study);
        model.addAttribute("day", Day.getAllDaysSelectedWith(study.getDay()));
        model.addAttribute("studyState", StudyState.getAllStudyStatesSelectedWith(study.getStudyState()));
        model.addAttribute("zone", Zone.getAllZonesSelectedWith(study.getZone()));
        return "studys/studys-update";
    }

    @GetMapping("/open")
    public String studyOpenList(@CurrentAccount Account account, @RequestParam(required = false) String title,
                                Model model, @AuthenticationPrincipal UserAccount userAccount,
                                @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable) {
        checkTopMenu(account, model);

        return getStudyList(model, title, StudyState.OPEN, userAccount, pageable);
    }

    @GetMapping("/close")
    public String studyCloseList(@CurrentAccount Account account, @RequestParam(required = false) String keyword,
                                 Model model, @AuthenticationPrincipal UserAccount userAccount,
                                 @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable) {
        checkTopMenu(account, model);

        return getStudyList(model, keyword, StudyState.CLOSE, userAccount, pageable);
    }

    @GetMapping("/end")
    public String studyEndList(@CurrentAccount Account account, @RequestParam(required = false) String keyword,
                               Model model, @AuthenticationPrincipal UserAccount userAccount,
                               @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable) {
        checkTopMenu(account, model);

        return getStudyList(model, keyword, StudyState.END, userAccount, pageable);
    }

    @GetMapping("/{id}/users")
    public String studyApplyUserList(@CurrentAccount Account account,
                                     @PathVariable Long id,
                                     Model model) {
        checkTopMenu(account, model);
        StudyInfoResultDto studyInfo = studyService.getStudyInfo(id);
        model.addAttribute("StudyInfoResultDto", studyInfo);

        return "studys/studys-users-list";
    }

    private void checkTopMenu(Account account, Model model) {
        account = accountService.findUserByEmail(account.getEmail());
        model.addAttribute("account", account);
        if (account.isMangerOf(account.getStudy())) {
            model.addAttribute("studyManager", account.getStudy());
        }

        if (account.isApplierOf(account.getStudy())) {
            model.addAttribute("studyApply", account.getStudy());
        }
    }

    private String getStudyList(Model model, String title, StudyState studyState,
                                @AuthenticationPrincipal UserAccount userAccount, Pageable pageable) {
        Page<StudyResultDto> studyResultDtos = null;

        studyResultDtos = studyService.getStudiesBySearch(title, studyState, userAccount.getAccount().getId(), pageable);

        model.addAttribute("studys", studyResultDtos);
        model.addAttribute("page", PageResultDto.of(studyResultDtos));
        model.addAttribute("studyState", StudyState.getTitleFrom(studyState));
        model.addAttribute("studyStateCode", studyState.toString().toLowerCase());

        return "studys/studys-list";
    }
}
