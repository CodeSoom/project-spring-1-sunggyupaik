package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.infra.account.AccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class AccountService {
    private final AccountRepository accountRepository;
    private final EmailAuthenticationRepository emailAuthenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final UploadFileService uploadFileService;
    private final RoleRepository roleRepository;

    public AccountService(AccountRepository accountRepository,
                          EmailAuthenticationRepository emailAuthenticationRepository,
                          PasswordEncoder passwordEncoder,
                          UploadFileService uploadFileService,
                          RoleRepository roleRepository
    ) {
        this.accountRepository = accountRepository;
        this.emailAuthenticationRepository = emailAuthenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.uploadFileService = uploadFileService;
        this.roleRepository = roleRepository;
    }

    public Account findUser(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public Account findUserByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountEmailNotFoundException(email));
    }

    public AccountResultDto getUser(Long id) {
        return accountRepository.findById(id)
                .map(AccountResultDto::of)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public AccountResultDto createUser(AccountCreateDto accountCreateDto, UploadFile uploadFile) {
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
        boolean nicknameDuplicated = accountRepository.existsByNickname(nickname);
        if (nicknameDuplicated) {
            throw new AccountNicknameDuplicatedException(nickname);
        }

        Account account = accountCreateDto.toEntity();
        if(uploadFile != null) {
            account.addUploadFile(uploadFile);
        }

        account.updatePassword(account.getPassword(), passwordEncoder);
        Account createdAccount = accountRepository.save(account);

        Role role = Role.builder()
                .email(account.getEmail())
                .name("USER")
                .build();
        roleRepository.save(role);

        deleteEmailAuthentication(emailAuthentication.getEmail());

        return AccountResultDto.of(createdAccount);
    }

    public AccountResultDto updateUser(Long id, AccountUpdateDto accountUpdateDto,
                                       UploadFile uploadFile) {
        Account account = findUser(id);

        String password = accountUpdateDto.getPassword();
        if (!account.isPasswordSameWith(password, passwordEncoder)) {
            throw new AccountPasswordBadRequestException();
        }

        String nickname = accountUpdateDto.getNickname();
        if (isNicknameDuplicated(id, nickname)) {
            throw new AccountNicknameDuplicatedException(nickname);
        }
        account.updateNickname(nickname);

        if(uploadFile != null) {
            uploadFileService.deleteUploadFile(accountUpdateDto.getSavedFileName());
            account.addUploadFile(uploadFile);
        }

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
        Account account = findUser(id);
        account.delete();

        return AccountResultDto.of(account);
    }

    public boolean isNicknameDuplicated(Long id, String nickname) {
        return accountRepository.existsByIdNotAndNickname(id, nickname);
    }

    public long countAllAccounts() {
        return accountRepository.findAll().size();
    }

    public AccountResultDto updatePassword(Long id, AccountUpdatePasswordDto accountUpdatePasswordDto) {
        Account account = findUser(id);

        String password = accountUpdatePasswordDto.getPassword();
        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new AccountPasswordBadRequestException();
        }

        String newPassword = accountUpdatePasswordDto.getNewPassword();
        String newPasswordConfirmed = accountUpdatePasswordDto.getNewPasswordConfirmed();
        if (!newPassword.equals(newPasswordConfirmed)) {
            throw new AccountNewPasswordNotMatchedException();
        }

        account.updatePassword(accountUpdatePasswordDto.getNewPassword(), passwordEncoder);

        return AccountResultDto.of(account);
    }
}
