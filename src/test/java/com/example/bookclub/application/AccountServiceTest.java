package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class AccountServiceTest {
    private static final Long EXISTED_ID = 1L;
    private static final String SETUP_NAME = "홍길동";
    private static final String SETUP_EMAIL = "abcd@naver.com";
    private static final String SETUP_NICKNAME = "abcd";
    private static final String SETUP_PASSWORD = "1234";
    private static final String SETUP_PROFILEIMAGE = "image";

    private static final Long CREATED_ID = 2L;
    private static final String CREATED_NAME = "김철수";
    private static final String CREATED_EMAIL = "qwer@naver.com";
    private static final String CREATED_NICKNAME = "qwer";
    private static final String CREATED_PASSWORD = "5678";
    private static final String CREATED_PROFILEIMAGE = "picture";

    private static final String UPDATED_NICKNAME = "qwer";
    private static final String NOT_EXISTED_PASSWORD = "5678";
    private static final String UPDATED_PASSWORD = "1234";

    private static final String UPDATED_PROFILEIMAGE = "picture";

    private static final String EXISTED_EMAIL = "abcd@naver.com";
    private static final String EXISTED_NICKNAME = "abcd";
    private static final String EXISTED_AUTHENTICATIONNUMBER = "12345";
    private static final String NOT_EXISTED_AUTHENTICATIONNUMBER = "67890";

    private Account setUpAccount;
    private Account createdAccount;
    private PasswordEncoder passwordEncoder;

    private AccountCreateDto accountCreateDto;
    private AccountCreateDto emailExistedAccountCreateDto;
    private AccountCreateDto nicknameExistedAccountCreateDto;
    private AccountUpdateDto accountUpdateDto;
    private AccountUpdateDto nicknameExistedAccountUpdateDto;
    private AccountUpdateDto passwordNotExistedAccountUpdateDto;
    private EmailAuthentication emailAuthentication;

    private AccountService accountService;
    private AccountRepository accountRepository;
    private EmailAuthenticationRepository emailAuthenticationRepository;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        emailAuthenticationRepository = mock(EmailAuthenticationRepository.class);
        passwordEncoder = new BCryptPasswordEncoder();
        accountService = new AccountService(accountRepository,
                emailAuthenticationRepository, passwordEncoder);

        setUpAccount = Account.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .email(SETUP_EMAIL)
                .nickname(SETUP_NICKNAME)
                .password(SETUP_PASSWORD)
                .profileImage(SETUP_PROFILEIMAGE)
                .build();

        createdAccount = Account.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        accountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .authenticationNumber(EXISTED_AUTHENTICATIONNUMBER)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(UPDATED_NICKNAME)
                .password(UPDATED_PASSWORD)
                .profileImage(UPDATED_PROFILEIMAGE)
                .build();

        emailExistedAccountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(EXISTED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        nicknameExistedAccountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(EXISTED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        nicknameExistedAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(SETUP_NICKNAME)
                .password(SETUP_PASSWORD)
                .profileImage(UPDATED_PROFILEIMAGE)
                .build();

        passwordNotExistedAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(SETUP_NICKNAME)
                .password(NOT_EXISTED_PASSWORD)
                .profileImage(SETUP_PROFILEIMAGE)
                .build();

        emailAuthentication = EmailAuthentication.builder()
                .email(CREATED_EMAIL)
                .authenticationNumber(EXISTED_AUTHENTICATIONNUMBER)
                .build();
    }

    @Test
    public void createWithValidAttribute() {
        given(accountRepository.save(any(Account.class))).willReturn(createdAccount);
        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        AccountResultDto accountResultDto = accountService.createUser(accountCreateDto);

        assertThat(accountResultDto.getId()).isEqualTo(CREATED_ID);
        assertThat(accountResultDto.getName()).isEqualTo(accountCreateDto.getName());
        assertThat(accountResultDto.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(accountResultDto.getNickname()).isEqualTo(accountCreateDto.getNickname());
        assertThat(accountResultDto.getPassword()).isEqualTo(accountCreateDto.getPassword());
        assertThat(accountResultDto.getProfileImage()).isEqualTo(accountCreateDto.getProfileImage());

        verify(emailAuthenticationRepository).delete(emailAuthentication);
    }

    @Test
    public void createWithDuplicatedEmail() {
        given(accountRepository.existsByEmail(EXISTED_EMAIL)).willReturn(true);

        assertThatThrownBy(() -> accountService.createUser(emailExistedAccountCreateDto))
                .isInstanceOf(AccountEmailDuplicatedException.class);
    }

    @Test
    public void createWithDuplicatedNickname() {
        given(accountRepository.existsByNickname(EXISTED_NICKNAME)).willReturn(true);

        assertThatThrownBy(() -> accountService.createUser(nicknameExistedAccountCreateDto))
                .isInstanceOf(AccountNicknameDuplicatedException.class);
    }

    @Test
    public void createWithNotValidAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(SETUP_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createUser(accountCreateDto))
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }

    @Test
    public void updateWithValidAttribute() {
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpAccount));

        AccountResultDto accountResultDto = accountService.updateUser(EXISTED_ID, accountUpdateDto);
        
        assertThat(accountResultDto.getNickname()).isEqualTo(accountUpdateDto.getNickname());
        assertThat(accountResultDto.getPassword()).isEqualTo(accountUpdateDto.getPassword());
        assertThat(accountResultDto.getProfileImage()).isEqualTo(accountUpdateDto.getProfileImage());
    }

    @Test
    public void updatedWithExistedNickname() {
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpAccount));

        assertThatThrownBy(() -> accountService.updateUser(EXISTED_ID, nicknameExistedAccountUpdateDto))
                .isInstanceOf(AccountNicknameDuplicatedException.class);
    }

    @Test
    public void updateWithNotValidPassword() {
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpAccount));

        assertThatThrownBy(() -> accountService.updateUser(EXISTED_ID, passwordNotExistedAccountUpdateDto))
                .isInstanceOf(AccountPasswordBadRequestException.class);
    }
}
