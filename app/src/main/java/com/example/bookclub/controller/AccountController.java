package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.study.query.StudyQueryService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.security.CurrentAccount;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * 사용자의 가입, 수정, 즐겨찾기 등 페이지를 요청한다
 */
@Controller
@RequestMapping("/users")
public class AccountController {
    private final StudyQueryService studyQueryService;
    private final AccountAuthenticationService accountAuthenticationService;

    public AccountController(StudyQueryService studyQueryService,
                             AccountAuthenticationService accountAuthenticationService) {
        this.studyQueryService = studyQueryService;
        this.accountAuthenticationService = accountAuthenticationService;
    }

    /**
     * 회원가입 페이지로 이동한다
     * 로그인이 되어 있다면 루트 페이지로 이동한다
     *
     * @param account 로그인한 사용자
     * @return 회원가입 페이지
     */
    @GetMapping("/save")
    public String accountSave(@CurrentAccount Account account) {
        if(account != null) {
            return "redirect:/";
        }

        return "users/users-save";
    }

    /**
     * 주어진 로그인한 사용자, 사용자 식별자에 해당하는 스터디 즐겨찾기 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 사용자 식별자
     * @param model 모델
     * @return 스터디 즐겨찾기 페이지
     * @throws AccessDeniedException 경로에 있는 사용자 식별자가 로그인한 사용자의 식별자가 아닌 경우
     */
    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/{id}/favorite")
    public String accountFavorite(@AuthenticationPrincipal UserAccount userAccount,
                                @PathVariable Long id, Model model) {
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        List<StudyApiDto.StudyFavoriteDto> studies = studyQueryService.getFavoriteStudies(savedAccount);

        model.addAttribute("StudyFavoriteDto", studies);

        return "users/users-favorite";
    }

    /**
     * 주어진 로그인한 사용자, 사용자 식별자에 해당하는 사용자 수정 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 사용자 식별자
     * @param model 모델
     * @return 사용자 수정 페이지
     * @throws AccessDeniedException 경로 사용자 식별자가 로그인한 사용자의 식별자가 아닌 경우
     */
    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/update/{id}")
    public String accountUpdate(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        return "users/users-update";
    }

    /**
     * 주어진 로그인한 사용자, 사용자 식별자에 해당하는 사용자 비밀번호 수정 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 사용자 식별자
     * @param model 모델
     * @return 사용자 비밀번호 수정 페이지
     * @throws AccessDeniedException 경로 사용자 식별자가 로그인한 사용자의 식별자가 아닌 경우
     */
    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/update/password/{id}")
    public String usersPasswordUpdate(@AuthenticationPrincipal UserAccount userAccount,
                                      @PathVariable Long id, Model model) {
        Account savedAccount = accountAuthenticationService.getAccountByEmail(userAccount.getAccount().getEmail());
        checkTopMenu(savedAccount, model);

        return "users/users-update-password";
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
