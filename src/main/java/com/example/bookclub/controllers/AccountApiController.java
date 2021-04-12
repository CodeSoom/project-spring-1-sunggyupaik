package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/api/users")
public class AccountApiController {
    private final AccountService accountService;
    private final UploadFileService uploadFileService;

    public AccountApiController(AccountService accountService,
                                UploadFileService uploadFileService) {
        this.accountService = accountService;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping("/{id}")
    public AccountResultDto detail(@PathVariable Long id) {
        Account account = accountService.getUser(id);
        return AccountResultDto.of(account);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResultDto create(HttpServletRequest request,
                                   @RequestPart(required = false) MultipartFile uploadFile,
                                   AccountCreateDto accountCreateDto) throws IOException {
        AccountResultDto account = accountService.createUser(accountCreateDto);
        if (uploadFile != null) {
            UploadFile file = uploadFileService.saveUploadFile(request, uploadFile, account.getId());
        }
        return account;
    }

    @PostMapping("/{id}")
    public AccountResultDto update(HttpServletRequest request,
                                   @RequestPart(required = false) MultipartFile uploadFile,
                                   @PathVariable Long id,
                                   AccountUpdateDto accountUpdateDto) throws IOException {
        AccountResultDto account = accountService.updateUser(id, accountUpdateDto);
        if (uploadFile != null) {
            UploadFile file = uploadFileService.saveUploadFile(request, uploadFile, account.getId());
        }
        return account;
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public AccountResultDto delete(@PathVariable Long id) {
        return accountService.deleteUser(id);
    }
}
