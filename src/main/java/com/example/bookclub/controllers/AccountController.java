package com.example.bookclub.controllers;

import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
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
    private final UploadFileService uploadFileService;

    public AccountController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

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
        if (account != null) {
            model.addAttribute("account", account);
            UploadFile uploadFile = uploadFileService.getUploadFile(account.getUploadFile().getId());
            model.addAttribute("uploadFile", uploadFile);
        }
        if (account !=null && account.getStudy() != null) {
            model.addAttribute("studyName", account.getStudy().getName());
        }
        if (account != null && !account.getId().equals(id)) {
            throw new AccessDeniedException("권한이 없습니다");
        }
        return "users/users-update";
    }
}
