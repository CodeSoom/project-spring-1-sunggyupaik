package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountCreateResultDto;
import com.example.bookclub.dto.AccountDeleteResultDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.dto.AccountUpdatePasswordResultDto;
import com.example.bookclub.dto.AccountUpdateResultDto;
import com.example.bookclub.security.CurrentAccount;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

/**
 * 회원가입, 정보수정, 사용자 비밀번호 변경, 삭제를 요청한다
 */
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

    /**
     * 주어진 사용자 식별자에 해당하는 사용자를 조회한다
     *
     * @param id 사용자 식별자
     * @return 조회한 사용자 정보
     */
    @GetMapping("/{id}")
    public AccountResultDto get(@PathVariable Long id) {
        return accountService.getAccount(id);
    }

    /**
     * 주어진 사용자 사진, 회원가입 정보로 사용자를 생성한다
     *
     * @param uploadFile 사용자 사진
     * @param accountCreateDto 회원가입 정보
     * @return 생성된 사용자 정보
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AccountCreateResultDto create(@RequestPart(required = false) MultipartFile uploadFile,
                                         AccountCreateDto accountCreateDto) {
        if (uploadFile == null) {
            return accountService.createAccount(accountCreateDto, null);
        }

        UploadFile accountFile = uploadFileService.upload(uploadFile);
        return accountService.createAccount(accountCreateDto, accountFile);
    }

    /**
     * 주어진 로그인한 사용자, 사용자 식별자, 사용자 사진, 변경할 사용자 정보로 사용자를 수정한다
     *
     * @param account 로그인한 사용자
     * @param id 사용자 식별자
     * @param uploadFile 사용자 사진
     * @param accountUpdateDto 변경 할 사용자 정보
     * @return 수정된 사용자 정보
     * @throws AccessDeniedException 경로 아이디와 로그인한 사용자의 아이디가 다른 경우
     */
    @PreAuthorize("#account.id == #id")
    @PostMapping("/{id}")
    public AccountUpdateResultDto update(@CurrentAccount Account account,
                                         @PathVariable Long id,
                                         @RequestPart(required = false) MultipartFile uploadFile,
                                         AccountUpdateDto accountUpdateDto) {
    if (uploadFile == null) {
            return accountService.updateAccount(id, accountUpdateDto, null);
        }

        UploadFile accountFile = uploadFileService.upload(uploadFile);
        return accountService.updateAccount(id, accountUpdateDto, accountFile);
    }

    /**
     * 주어진 로그인한 사용자, 사용자 식별자, 변경 할 비밀번호로 사용자 비밀번호를 변경한다
     *
     * @param account 로그인한 사용자
     * @param id 사용자 식별자
     * @param accountUpdatePasswordDto 변경 할 사용자 비밀번호
     * @return 비밀번호가 수정된 사용자 정보
     */
    @PreAuthorize("#account.id == #id")
    @PatchMapping("/{id}/password")
    public AccountUpdatePasswordResultDto updatePassword(@CurrentAccount Account account,
                                                         @PathVariable Long id,
                                                         @Valid @RequestBody AccountUpdatePasswordDto accountUpdatePasswordDto) {
        return accountService.updatePassword(id, accountUpdatePasswordDto);
    }

    /**
     * 주어진 로그인한 사용자, 사용자 식별자로 사용자를 삭제한다
     *
     * @param account 로그인한 사용자
     * @param id 사용자 아이디
     * @return 삭제한 사용자 정보
     */
    @PreAuthorize("#account.id == #id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public AccountDeleteResultDto delete(@CurrentAccount Account account,
                                         @PathVariable Long id) {
        return accountService.deleteAccount(id);
    }
}
