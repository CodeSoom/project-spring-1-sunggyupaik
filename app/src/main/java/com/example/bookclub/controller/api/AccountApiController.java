package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class AccountApiController {
    private final AccountService accountService;
    private final UploadFileService uploadFileService;

    public AccountApiController(AccountService accountService, UploadFileService uploadFileService) {
        this.accountService = accountService;
        this.uploadFileService = uploadFileService;
    }

    @GetMapping("/{id}")
    public AccountResultDto get(@PathVariable Long id) {
        return accountService.getUser(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResultDto create(@RequestPart(required = false) MultipartFile uploadFile,
                                   AccountCreateDto accountCreateDto) {
        if (uploadFile == null) {
            return accountService.createUser(accountCreateDto, null);
        }

        UploadFile accountFile = uploadFileService.upload(uploadFile);
        return accountService.createUser(accountCreateDto, accountFile);
    }

    @PreAuthorize("#account.id == #id")
    @PostMapping("/{id}")
    public AccountResultDto update(@CurrentAccount Account account,
                                   @PathVariable Long id,
                                   @RequestPart(required = false) MultipartFile uploadFile,
                                   AccountUpdateDto accountUpdateDto) {
        if (uploadFile == null) {
            return accountService.updateUser(id, accountUpdateDto, null);
        }

        UploadFile accountFile = uploadFileService.upload(uploadFile);
        return accountService.updateUser(id, accountUpdateDto, accountFile);
    }

    @PreAuthorize("#account.id == #id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public AccountResultDto delete(@CurrentAccount Account account,
                                   @PathVariable Long id) {
        return accountService.deleteUser(id);
    }
}
