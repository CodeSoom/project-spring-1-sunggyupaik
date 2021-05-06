package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
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
    private static final Long SETUP_ID = 1L;
    private static final String SETUP_NAME = "setupName";
    private static final String SETUP_EMAIL = "setupEmail";
    private static final String SETUP_NICKNAME = "setupNickName";
    private static final String SETUP_PASSWORD = "setupPassword";

    private static final Long CREATED_ID = 2L;
    private static final String CREATED_NAME = "createdName";
    private static final String CREATED_EMAIL = "createdEmail";
    private static final String CREATED_NICKNAME = "createdNickName";
    private static final String CREATED_PASSWORD = "createdPassword";

    private static final String UPDATED_NICKNAME = "updatedNickName";
    private static final String NOT_VALID_PASSWORD = "notValidPassword";
    private static final String UPDATED_PASSWORD = "updatedPassword";

    private static final Long NOT_EXISTED_ID = 100L;
    private static final String DUPLICATED_EMAIL = "existedEmail";
    private static final String DUPLICATED_NICKNAME = "existedNickName";
    private static final String CREATED_AUTHENTICATIONNUMBER = "existedAuthentication";
    private static final String NOT_EXISTED_AUTHENTICATIONNUMBER = "notExistedAuthentication";

    private Account setUpAccount;
    private Account notEncodedCreatedAccount;
    private Account createdAccount;
    private PasswordEncoder passwordEncoder;

    private AccountCreateDto accountCreateDto;
    private AccountCreateDto authenticationNumberNotMatchedAccountCreateDto;
    private AccountCreateDto emailExistedAccountCreateDto;
    private AccountCreateDto nicknameExistedAccountCreateDto;
    private AccountUpdateDto accountUpdateDto;
    private AccountUpdateDto nicknameDuplicatedAccountUpdateDto;
    private AccountUpdateDto passwordNotValidAccountUpdateDto;
    private AccountUpdatePasswordDto accountUpdatePasswordDto;
    private AccountUpdatePasswordDto newPasswordNotMatchedDto;
    private AccountUpdatePasswordDto passwordNotMatchedDto;
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
                .id(SETUP_ID)
                .name(SETUP_NAME)
                .email(SETUP_EMAIL)
                .nickname(SETUP_NICKNAME)
                .password(passwordEncoder.encode(SETUP_PASSWORD))
                .build();

        notEncodedCreatedAccount = Account.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        createdAccount = Account.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(passwordEncoder.encode(CREATED_PASSWORD))
                .build();

        accountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .authenticationNumber(CREATED_AUTHENTICATIONNUMBER)
                .build();

        authenticationNumberNotMatchedAccountCreateDto = AccountCreateDto.builder()
                .authenticationNumber(NOT_EXISTED_AUTHENTICATIONNUMBER)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(UPDATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        emailExistedAccountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(DUPLICATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        nicknameExistedAccountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(DUPLICATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        nicknameDuplicatedAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(DUPLICATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        passwordNotValidAccountUpdateDto = AccountUpdateDto.builder()
                .nickname(SETUP_NICKNAME)
                .password(NOT_VALID_PASSWORD)
                .build();

        accountUpdatePasswordDto = AccountUpdatePasswordDto.builder()
                .password(CREATED_PASSWORD)
                .newPassword(UPDATED_PASSWORD)
                .newPasswordConfirmed(UPDATED_PASSWORD)
                .build();

        newPasswordNotMatchedDto = AccountUpdatePasswordDto.builder()
                .password(CREATED_PASSWORD)
                .newPassword(UPDATED_PASSWORD)
                .newPasswordConfirmed(SETUP_PASSWORD)
                .build();

        passwordNotMatchedDto = AccountUpdatePasswordDto.builder()
                .password("")
                .newPassword(UPDATED_PASSWORD)
                .newPasswordConfirmed(UPDATED_PASSWORD)
                .build();

        emailAuthentication = EmailAuthentication.builder()
                .email(CREATED_EMAIL)
                .authenticationNumber(CREATED_AUTHENTICATIONNUMBER)
                .build();
    }

    @Test
    public void detailWithExistedId() {
        given(accountRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpAccount));

        Account account = accountService.getUser(SETUP_ID);

        assertThat(account.getId()).isEqualTo(SETUP_ID);
    }

    @Test
    public void detailWithNotExistedId() {
        given(accountRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.getUser(NOT_EXISTED_ID))
                .isInstanceOf(AccountNotFoundException.class);
    }

    @Test
    public void createWithValidAttribute() {
        given(accountRepository.save(any(Account.class))).willReturn(notEncodedCreatedAccount);
        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        AccountResultDto accountResultDto = accountService.createUser(accountCreateDto);

        assertThat(accountResultDto.getId()).isEqualTo(CREATED_ID);
        assertThat(accountResultDto.getName()).isEqualTo(accountCreateDto.getName());
        assertThat(accountResultDto.getEmail()).isEqualTo(accountCreateDto.getEmail());
        assertThat(accountResultDto.getNickname()).isEqualTo(accountCreateDto.getNickname());
        assertThat(passwordEncoder.matches(
                accountCreateDto.getPassword(),
                accountResultDto.getPassword())
        ).isTrue();
        assertThat(emailAuthentication.getAuthenticationNumber())
                .isEqualTo(accountCreateDto.getAuthenticationNumber());

        verify(emailAuthenticationRepository).delete(emailAuthentication);
    }

    @Test
    public void createWithDuplicatedEmail() {
        given(accountRepository.existsByEmail(DUPLICATED_EMAIL)).willReturn(true);

        assertThatThrownBy(() -> accountService.createUser(emailExistedAccountCreateDto))
                .isInstanceOf(AccountEmailDuplicatedException.class);
    }

    @Test
    public void createWithNotMatchedAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        assertThatThrownBy(() -> accountService.createUser(authenticationNumberNotMatchedAccountCreateDto))
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }

    @Test
    public void createWithNotExistedAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.createUser(accountCreateDto))
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }

    @Test
    public void createWithDuplicatedNickname() {
        given(accountRepository.existsByNickname(DUPLICATED_NICKNAME)).willReturn(true);
        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL)).willReturn(Optional.of(emailAuthentication));

        assertThatThrownBy(() -> accountService.createUser(nicknameExistedAccountCreateDto))
                .isInstanceOf(AccountNicknameDuplicatedException.class);
    }

    @Test
    public void updateWithValidAttribute() {
        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));

        AccountResultDto accountResultDto = accountService.updateUser(CREATED_ID, accountUpdateDto);

        assertThat(accountResultDto.getNickname()).isEqualTo(accountUpdateDto.getNickname());
        assertThat(passwordEncoder.matches(
                accountUpdateDto.getPassword(), accountResultDto.getPassword())
        ).isTrue();
    }

    @Test
    public void updatedWithDuplicatedNickname() {
        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));
        given(accountRepository.existsByIdNotAndNickname(CREATED_ID, DUPLICATED_NICKNAME)).willReturn(true);

        assertThatThrownBy(() -> accountService.updateUser(CREATED_ID, nicknameDuplicatedAccountUpdateDto))
                .isInstanceOf(AccountNicknameDuplicatedException.class);
    }

    @Test
    public void updateWithNotValidPassword() {
        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));

        assertThatThrownBy(() -> accountService.updateUser(CREATED_ID, passwordNotValidAccountUpdateDto))
                .isInstanceOf(AccountPasswordBadRequestException.class);
    }

    @Test
    public void updatePasswordWithValidAttribute() {
        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));

        AccountResultDto accountResultDto = accountService.updateUserPassword(CREATED_ID, accountUpdatePasswordDto);

        assertThat(passwordEncoder.matches(
                accountUpdatePasswordDto.getNewPassword(),
                accountResultDto.getPassword())
        ).isTrue();
    }

    @Test
    public void updatePasswordWithNotMatchedNewPassword() {
        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));

        assertThatThrownBy(() -> accountService.updateUserPassword(CREATED_ID, newPasswordNotMatchedDto))
                .isInstanceOf(AccountNewPasswordNotMatchedException.class);
    }

    @Test
    public void updatePasswordWithNotValidPassword() {
        given(accountRepository.findById(CREATED_ID)).willReturn(Optional.of(createdAccount));

        assertThatThrownBy(() -> accountService.updateUserPassword(CREATED_ID, passwordNotMatchedDto))
                .isInstanceOf(AccountPasswordBadRequestException.class);
    }

    @Test
    public void deleteAccount() {
        given(accountRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpAccount));

        AccountResultDto accountResultDto = accountService.deleteUser(SETUP_ID);

        assertThat(accountResultDto.getId()).isEqualTo(SETUP_ID);
        assertThat(accountResultDto.isDeleted()).isTrue();
    }
}
