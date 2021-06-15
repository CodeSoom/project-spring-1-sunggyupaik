package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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
        if (accountRepository.existsByNickname(nickname)) {
            throw new AccountNicknameDuplicatedException(nickname);
        }

        Account account = accountCreateDto.toEntity();
        if(uploadFile != null) {
            account.addUploadFile(uploadFile);
        }

        Account createdAccount = accountRepository.save(account);
        createdAccount.updatePassword(createdAccount.getPassword(), passwordEncoder);
        deleteEmailAuthentication(emailAuthentication.getEmail());

        return AccountResultDto.of(createdAccount);
    }

    public AccountResultDto updateUser(Long id, AccountUpdateDto accountUpdateDto, UploadFile uploadFile) {
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
            UploadFile accountImage = account.getUploadFile();
            uploadFileService.deleteUploadFile(accountImage.getFileName());
            account.addUploadFile(uploadFile);
        }

        login(account);

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

        UploadFile uploadFile = account.getUploadFile();
        if (uploadFile != null) {
            uploadFileService.deleteUploadFile(uploadFile.getFileName());
        }
        account.delete();

        return AccountResultDto.of(account);
    }

    public boolean isNicknameDuplicated(Long id, String nickname) {
        return accountRepository.existsByIdNotAndNickname(id, nickname);
    }

    public long countAllAccounts() {
        return accountRepository.findAll().size();
    }

    public AccountResultDto updateUserPassword(Long id, AccountUpdatePasswordDto accountUpdatePasswordDto) {
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

    public void signup(SessionCreateDto sessionCreateDto) {
        Account account = authenticateUser(sessionCreateDto); // LAZY 강제 초기화를 위한 연관관계 내부 명시적 조인?
        List<GrantedAuthority> authorities = getAllAuthorities(sessionCreateDto.getEmail());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account, authorities), account.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }

    public List<GrantedAuthority> getAllAuthorities(String email) {
        List<Role> roles = roleRepository.findAllByEmail(email);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    public Account authenticateUser(SessionCreateDto sessionCreateDto) {
        String email = sessionCreateDto.getEmail();
        String password = sessionCreateDto.getPassword();

        return accountRepository.findByEmail(email)
                .filter(u -> u.authenticate(password, passwordEncoder))
                .orElseThrow(AuthenticationBadRequestException::new);
    }

    public void login(Account account) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account, authorities), account.getPassword(), authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
