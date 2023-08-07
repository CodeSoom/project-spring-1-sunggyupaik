package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.study.StudyService;
import com.example.bookclub.common.exception.account.AccountNotManagerOfStudyException;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Day;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.domain.study.Zone;
import com.example.bookclub.dto.PageResultDto;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.dto.StudyDto;
import com.example.bookclub.security.UserAccount;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 스터디 조회, 생성, 수정 페이지를 요청한다
 */
@Controller
@RequestMapping("/studies")
public class StudyController {
    private final StudyService studyService;
    private final AccountAuthenticationService accountAuthenticationService;

    public StudyController(StudyService studyService,
                           AccountAuthenticationService accountAuthenticationService) {
        this.studyService = studyService;
        this.accountAuthenticationService = accountAuthenticationService;
    }

    /**
     * 주어진 로그인한 사용자와 스터디 식별자에 해당하는 스터디 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @param model 모델
     * @return 스터디 조회 페이지
     */
    @GetMapping("/{id}")
    public String studyDetail(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        StudyApiDto.StudyDetailResultDto detailedStudy = studyService.getDetailedStudy(userAccount, id);
        model.addAttribute("detailedStudy", detailedStudy);

        return "studies/studies-detail";
    }

    /**
     * 주어진 로그인한 사용자와 책이름, 책사진에 해당하는 스터디 생성 페이지로 이동한다
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

        StudyDto.StudyCreateInfoDto studyCreateInfoDto = StudyDto.StudyCreateInfoDto.of(
                bookName, bookImage, Day.getAllDays(), StudyState.getAllStudyStates(), Zone.getAllZones()
        );

        model.addAttribute("StudyCreateInfoDto", studyCreateInfoDto);

        return "studies/studies-save";
    }

    /**
     * 주어진 로그인한 사용자와, 스터디 식별자에 해당하는 스터디의 수정 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @param model 모델
     * @return 스터디 수정 페이지
     * @throws AccessDeniedException 경로 아이디와 로그인한 사용자의 생성 스터디 아이디가 다른 경우
     */
    //@PreAuthorize("@studyManagerCheck.isManagerOfStudy(#userAccount.account)")
    @GetMapping("/update/{id}")
    public String studyUpdate(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        Study study = studyService.getStudy(id);

        if(study == null || !(userAccount.getAccount().getEmail().equals(study.getEmail()))) {
            throw new AccountNotManagerOfStudyException();
        }

        StudyDto.StudyUpdateInfoDto studyUpdateInfoDto = StudyDto.StudyUpdateInfoDto.of(study);
        model.addAttribute("StudyUpdateInfoDto", studyUpdateInfoDto);

        return "studies/studies-update";
    }

    /**
     * 주어진 로그인한 사용자, 페이징 정보에 해당하는 모집중 스터디 조회 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param model 모델
     * @param pageable 페이징
     * @param search 검색 제목
     * @return 모집중 스터디 조회 페이지
     */
    @GetMapping("/open")
    public String studyOpenList(@AuthenticationPrincipal UserAccount userAccount, Model model,
                                @PageableDefault(size=10, sort="id", direction= Sort.Direction.ASC) Pageable pageable,
                                @RequestParam(required = false) String search) {
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        return getStudyList(savedAccount, pageable, model, search, StudyState.OPEN);
    }

    /**
     * 주어진 로그인한 사용자, 페이징 정보에 해당하는 진행중 스터디 조회 페이지로 이동한다
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
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        return getStudyList(savedAccount, pageable, model, title, StudyState.CLOSE);
    }

    /**
     *  주어진 로그인한 사용자, 페이징 정보에 해당하는 종료 스터디 조회 페이지로 이동한다
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
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        return getStudyList(savedAccount, pageable, model, title, StudyState.END);
    }

    /**
     * 주어진 로그인한 사용자, 스터디 식별자에 해당하는 스터디 지원현황 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @param model 모델
     * @return 스터디 지원현황 페이지
     */
    @GetMapping("/{id}/users")
    public String studyApplyUserList(@AuthenticationPrincipal UserAccount userAccount,
                                     @PathVariable Long id, Model model) {
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        StudyDto.StudyInfoResultDto studyInfo = studyService.getStudyInfo(id);
        model.addAttribute("StudyInfoResultDto", studyInfo);

        return "studies/studies-users-list";
    }

    /**
     * 주어진 로그인한 사용자, 페이징 정보, 제목, 스터디 상태에 해당하는 스터디 검색 페이지로 이동한다
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
        List<StudyApiDto.StudyResultDto> studyResultDto =
                studyService.getStudiesBySearch(title, studyState, account, pageable);

        StudyDto.StudyListInfoDto studyListInfoDto = StudyDto.StudyListInfoDto.of(studyResultDto, studyState, title);
        long total = studyService.getStudiesBySearchCount(title, studyState);

        model.addAttribute("StudyListInfoDto", studyListInfoDto);
        model.addAttribute("page", PageResultDto.of(new PageImpl<>(studyResultDto, pageable, total)));

        return "studies/studies-list";
    }

    /**
     * 로그인한 사용자의 스터디 개설, 참여 여부를 확인한다
     *
     * @param account 로그인한 사용자
     * @param model 모델
     */
    private void checkTopMenu(Account account, Model model) {
        if (account.isMangerOf(account.getStudy()))
            model.addAttribute("studyManager", account.getStudy());

        if (account.isApplierOf(account.getStudy()))
            model.addAttribute("studyApply", account.getStudy());

        model.addAttribute("account", account);
    }
}
