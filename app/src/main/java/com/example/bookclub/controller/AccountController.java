package com.example.bookclub.controller;

import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.StudyFavoriteDto;
import com.example.bookclub.repository.study.JpaStudyRepository;
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
import java.util.stream.Collectors;

/**
 * 사용자의 가입, 수정, 즐겨찾기 등 페이지를 요청한다
 */
@Controller
@RequestMapping("/users")
public class AccountController {
    private final JpaStudyRepository studyRepository;

    public AccountController(JpaStudyRepository studyRepository) {
        this.studyRepository = studyRepository;
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
     * 스터디 즐겨찾기 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 로그인한 사용자 번호
     * @param model 모델
     * @return 스터디 즐겨찾기 페이지
     * @throws AccessDeniedException 경로 아이디가 로그인한 사용자의 아이디가 아닌 경우
     */
    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/{id}/favorite")
    public String accountFavorite(@AuthenticationPrincipal UserAccount userAccount,
                                @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        List<Long> favoriteStudyIds = userAccount.getAccount().getFavorites()
                .stream().filter(favorite -> favorite.getAccount().getId().equals(id))
                .map(favorite -> favorite.getStudy().getId())
                .collect(Collectors.toList());

        List<StudyFavoriteDto> studies = studyRepository.findByFavoriteStudies(favoriteStudyIds);

        model.addAttribute("StudyFavoriteDto", studies);

        return "users/users-favorite";
    }

    /**
     * 사용자 수정 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 로그인한 사용자 아이디
     * @param model 모델
     * @return 사용자 수정 페이지
     * @throws AccessDeniedException 경로 아이디가 로그인한 사용자의 아이디가 아닌 경우
     */
    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/update/{id}")
    public String accountUpdate(@AuthenticationPrincipal UserAccount userAccount,
                              @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        return "users/users-update";
    }

    /**
     * 사용자 비밀번호 수정 페이지로 이동한다
     *
     * @param userAccount 로그인한 사용자
     * @param id 그인한 사용자 아이디
     * @param model 모델
     * @return 사용자 비밀번호 수정 페이지
     * @throws AccessDeniedException 경로 아이디가 로그인한 사용자의 아이디가 아닌 경우
     */
    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/update/password/{id}")
    public String usersPasswordUpdate(@AuthenticationPrincipal UserAccount userAccount,
                                      @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

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
