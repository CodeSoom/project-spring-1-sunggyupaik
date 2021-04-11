package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountService(AccountRepository accountRepository,
                          EmailAuthenticationRepository emailAuthenticationRepository,
                          PasswordEncoder passwordEncoder
    ) {
        this.accountRepository = accountRepository;
        this.emailAuthenticationRepository = emailAuthenticationRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Account getUser(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public AccountResultDto createUser(AccountCreateDto accountCreateDto) {
        String email = accountCreateDto.getEmail();
        if (accountRepository.existsByEmail(email)) {
            throw new AccountEmailDuplicatedException(email);
        }

        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        String authenticationNumber = emailAuthentication.getAuthenticationNumber();
        if (!emailAuthentication.isSameWith(authenticationNumber)) {
            throw new EmailNotAuthenticatedException(authenticationNumber);
        }

        String nickname = accountCreateDto.getNickname();
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountNicknameDuplicatedException(nickname);
        }

        Account account = accountCreateDto.toEntity();
        Account createdAccount = accountRepository.save(account);
        createdAccount.updatePassword(createdAccount.getPassword(), passwordEncoder);
        deleteEmailAuthentication(emailAuthentication.getEmail());

        return AccountResultDto.of(createdAccount);
    }

    public AccountResultDto updateUser(Long id, AccountUpdateDto accountUpdateDto) {
        Account account = getUser(id);

        String password = accountUpdateDto.getPassword();
        if (!account.isPasswordSameWith(password, passwordEncoder)) {
            throw new AccountPasswordBadRequestException();
        }

        String nickname = accountUpdateDto.getNickname();
        if (isNicknameDuplicated(id, nickname)) {
            throw new AccountNicknameDuplicatedException(nickname);
        }

        UploadFile uploadFile = accountUpdateDto.getUploadFile();
        String newPassword = accountUpdateDto.getNewPassword();
        account.updateWith(nickname, newPassword, uploadFile);

        return AccountResultDto.of(account);
    }

    public EmailAuthentication getAuthenticationNumber(String email) {
        return emailAuthenticationRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotAuthenticatedException(email));
    }

    public void deleteEmailAuthentication(String email) {
        EmailAuthentication emailAuthentication = getAuthenticationNumber(email);
        emailAuthenticationRepository.delete(emailAuthentication);
    }

    public AccountResultDto deleteUser(Long id) {
        Account account = getUser(id);

        account.delete();

        return AccountResultDto.of(account);
    }

    public boolean isNicknameDuplicated(Long id, String nickname) {
        return accountRepository.existsByIdNotAndNickname(id, nickname);
    }

    public long countAllAccounts() {
        return accountRepository.findAll().size();
    }
}
