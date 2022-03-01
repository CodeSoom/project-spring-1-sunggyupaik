package com.example.bookclub.controller;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.StudyFavoriteResultDto;
import com.example.bookclub.repository.study.JpaStudyRepository;
import com.example.bookclub.security.CurrentAccount;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/users")
public class AccountController {
    private final AccountAuthenticationService accountAuthenticationService;
    private final JpaStudyRepository studyRepository;

    public AccountController(AccountAuthenticationService accountAuthenticationService,
                             JpaStudyRepository studyRepository) {
        this.accountAuthenticationService = accountAuthenticationService;
        this.studyRepository = studyRepository;
    }

    @GetMapping("/save")
    public String usersSave(@CurrentAccount Account account) {
        if(account != null) {
            return "redirect:/";
        }
        return "users/users-save";
    }

    @PreAuthorize("#userAccount.account.id == #id")
    @GetMapping("/{id}/favorite")
    public String usersFavorite(@AuthenticationPrincipal UserAccount userAccount,
                                @PathVariable Long id, Model model) {
        checkTopMenu(userAccount.getAccount(), model);

        List<Long> favoriteStudyIds = userAccount.getAccount().getFavorites()
                .stream().filter(favorite -> favorite.getAccount().getId().equals(id))
                .map(favorite -> favorite.getStudy().getId())
                .collect(Collectors.toList());

        List<StudyFavoriteResultDto> studies = studyRepository.findByFavoriteStudies(favoriteStudyIds);

        model.addAttribute("StudyFavoriteResultDto", studies);
        model.addAttribute("account", userAccount.getAccount());
        checkTopMenu(userAccount.getAccount(), model);

        return "users/users-favorite";
    }

    @PreAuthorize("#account.id == #id")
    @GetMapping("/update/{id}")
    public String usersUpdate(@CurrentAccount Account account,
                              @PathVariable Long id, Model model) {
        account = accountAuthenticationService.getAccountByEmail(account.getEmail());
        model.addAttribute("account", account);
        checkTopMenu(account, model);
        return "users/users-update";
    }

    @PreAuthorize("#account.id == #id")
    @GetMapping("/update/password/{id}")
    public String usersPasswordUpdate(@CurrentAccount Account account,
                                      @PathVariable Long id, Model model) {
        model.addAttribute("account", account);
        checkTopMenu(account, model);
        return "users/users-update-password";
    }

    private void checkTopMenu(Account account, Model model) {
        if (account.isMangerOf(account.getStudy()))
            model.addAttribute("studyManager", account.getStudy());

        if (account.isApplierOf(account.getStudy()))
            model.addAttribute("studyApply", account.getStudy());
    }
}
