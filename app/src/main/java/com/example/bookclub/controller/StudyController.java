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
import com.example.bookclub.dto.StudyListInfoDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateInfoDto;
import com.example.bookclub.security.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 스터디 조회, 생성, 수정 페이지를 요청한다
 */
@Controller
@RequestMapping("/studies")
public class StudyController {
    private final StudyService studyService;
    private final AccountService accountService;

    public StudyController(StudyService studyService,
                           AccountService accountService) {
        this.studyService = studyService;
        this.accountService = accountService;
    }

    /**
     * 스터디 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 아이디
     * @param model 모델
     * @return 스터디 조회 페이지
     */
    @GetMapping("/{id}")
    public String studyDetail(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        StudyDetailResultDto detailedStudy = studyService.getDetailedStudy(userAccount, id);
        model.addAttribute("detailedStudy", detailedStudy);

        Study study = studyService.getStudy(id);
        if (study.isAlreadyStarted() || study.isNotOpened()) {
            return "studies/studies-detail";
        }

        return "studies/studies-detail";
    }

    /**
     * 스터디 생성 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param bookName 책이름
     * @param bookImage 책사진 주소
     * @param model 모델
     * @return 스터디 생성 페이지지
     */
   @GetMapping("/save")
    public String studySave(@AuthenticationPrincipal UserAccount userAccount,
                            @RequestParam(defaultValue = "") String bookName,
                            @RequestParam(defaultValue = "") String bookImage,
                            Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        StudyCreateInfoDto studyCreateInfoDto = StudyCreateInfoDto.of(
                bookName, bookImage, Day.getAllDays(), StudyState.getAllStudyStates(), Zone.getAllZones()
        );

        model.addAttribute("StudyCreateInfoDto", studyCreateInfoDto);

        return "studies/studies-save";
    }

    /**
     * 스터디 수정 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 로그인한 사용자의 생성 스터디 아이디
     * @param model 모델
     * @return 스터디 수정 페이지
     * @throws AccessDeniedException 경로 아이디와 로그인한 사용자의 생성 스터디 아이디가 다른 경우
     */
    @PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @GetMapping("/update/{id}")
    public String studyUpdate(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        Study study = studyService.getStudy(id);

        StudyUpdateInfoDto studyUpdateInfoDto = StudyUpdateInfoDto.of(study);
        model.addAttribute("StudyUpdateInfoDto", studyUpdateInfoDto);

        return "studies/studies-update";
    }

    /**
     * 모집중 스터디 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @param pageable 페이징
     * @param title 검색 제목
     * @return 모집중 스터디 조회 페이지
     */
    @GetMapping("/open")
    public String studyOpenList(@AuthenticationPrincipal UserAccount userAccount, Model model,
                                @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable,
                                @RequestParam(required = false) String title) {
        checkTopMenu(userAccount.getAccount(), model);

        return getStudyList(userAccount.getAccount(), pageable, model, title, StudyState.OPEN);
    }

    /**
     * 진행중 스터디 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @param pageable 페이징
     * @param title 검색 제목
     * @return 진행중 스터디 조회 페이지
     */
    @GetMapping("/close")
    public String studyCloseList(@AuthenticationPrincipal UserAccount userAccount, Model model,
                                 @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable,
                                 @RequestParam(required = false) String title) {
        checkTopMenu(userAccount.getAccount(), model);

        return getStudyList(userAccount.getAccount(), pageable, model, title, StudyState.CLOSE);
    }

    /**
     *  종료 스터디 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @param pageable 페이징
     * @param title 검색 제목
     * @return 종료 스터디 조회 페이지
     */
    @GetMapping("/end")
    public String studyEndList(@AuthenticationPrincipal UserAccount userAccount, Model model,
                               @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable,
                               @RequestParam(required = false) String title) {
        checkTopMenu(userAccount.getAccount(), model);

        return getStudyList(userAccount.getAccount(), pageable, model, title, StudyState.END);
    }

    /**
     * 스터디 지원현황 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 로그인한 사용자가 생성한 스터디 아이디
     * @param model 모델
     * @return 스터디 지원현황 페이지
     */
    @GetMapping("/{id}/users")
    public String studyApplyUserList(@AuthenticationPrincipal UserAccount userAccount,
                                     @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        StudyInfoResultDto studyInfo = studyService.getStudyInfo(id);
        model.addAttribute("StudyInfoResultDto", studyInfo);

        return "studies/studies-users-list";
    }

    /**
     * 주어진 페이징, 검색어, 스터디 상태로 스터디를 검색한다
     *
     * @param account 로그인한 사용자
     * @param pageable 페이징
     * @param model 모델
     * @param title 검색 제목
     * @param studyState 스터디 상태
     * @return 검색한 스터디 조회 페이지
     */
    private String getStudyList(Account account, Pageable pageable,
                                Model model, String title, StudyState studyState) {
        Page<StudyResultDto> studyResultDto = studyService.getStudiesBySearch(title, studyState, account.getId(), pageable);
        StudyListInfoDto studyListInfoDto = StudyListInfoDto.of(studyResultDto, studyState, title);

        model.addAttribute("StudyListInfoDto", studyListInfoDto);
        model.addAttribute("page", PageResultDto.of(studyResultDto));

        return "studies/studies-list";
    }

    /**
     * 로그인한 사용자의 스터디 개설, 참여 여부를 확인한다
     *
     * @param account 로그인한 사용자
     * @param model 모델
     */
    private void checkTopMenu(Account account, Model model) {
        if (account.isMangerOf(account.getStudy())) {
            model.addAttribute("studyManager", account.getStudy());
        }

        if (account.isApplierOf(account.getStudy())) {
            model.addAttribute("studyApply", account.getStudy());
        }

        model.addAttribute("account", account);
    }
}
