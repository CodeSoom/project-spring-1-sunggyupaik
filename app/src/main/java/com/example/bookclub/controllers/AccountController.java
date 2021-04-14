package com.example.bookclub.controllers;

import com.example.bookclub.domain.Account;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/users")
public class AccountController {
    @GetMapping("/save")
    public String usersSave(@CurrentAccount Account account, Model model) {
        if (account != null) {
            model.addAttribute("account", account);
        }
        return "users/users-save";
    }

    @GetMapping("/update/{id}")
    public String usersUpdate(@CurrentAccount Account account,
                              @PathVariable Long id, Model model) {
        if (account == null || !account.getId().equals(id)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
        model.addAttribute("account", account);

        if (account.getStudy() != null &&
                account.getStudy().getEmail().equals(account.getEmail())) {
            model.addAttribute("admin", account.getStudy());
        }

        return "users/users-update";
    }
}
