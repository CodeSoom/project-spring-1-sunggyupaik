package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.dto.ParseResultDto;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import com.example.bookclub.errors.InvalidTokenException;
import com.example.bookclub.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789010";
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
            "eyJzdWIiOiJzZXRVcEVtYWlsIiwidXNlcklkIjoxLCJleHAiOjE2MTcxNzY2NjF9." +
            "FtWghIoD9JCy8eODomAzagXfPxWOrV-xQBvhfgNhsJo";
    private static final String INVALID_TOKEN = "ayJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9." +
            "ayJzdWIiOiJzZXRVcEVtYWlsIiwidXNlcklkIjoxLCJleHAiOjE2MTcxNzY2NjF9." +
            "FtWghIoD9JCy8eODomAzagXfPxWOrV-xQBvhfgNhsJO";
    private static final Long EXISTED_ID = 1L;
    private static final String EXISTED_EMAIL = "setUpEmail";
    private static final String EXISTED_PASSWORD = "12345678";
    private static final String DELETED_EMAIL = "deletedEmail";
    private static final String EXISTED_ACCESSTOKEN = "setUpaccessToken";

    private static final String NOT_EXISTED_EMAIL = "invalidEmail";
    private static final String NOT_EXISTED_PASSWORD = "invalidPassword";

    private Account setUpAccount;
    private Account deletedAccount;
    private SessionCreateDto sessionCreateDto;
    private SessionCreateDto NotExistedEmailDto;
    private SessionCreateDto NotExistedPasswordDto;
    private SessionCreateDto DeletedEmailDto;

    private SessionResultDto sessionResultDto;
    private Role userRole;


    private AccountRepository accountRepository;
    private JwtUtil jwtUtil;
    private AuthenticationService authenticationService;
    private RoleRepository roleRepository;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        accountRepository = mock(AccountRepository.class);
        roleRepository = mock(RoleRepository.class);
        jwtUtil = new JwtUtil(SECRET);
        passwordEncoder = new BCryptPasswordEncoder();
        authenticationService = new AuthenticationService(
                accountRepository, roleRepository, jwtUtil, passwordEncoder);

        setUpAccount = Account.builder()
                .id(EXISTED_ID)
                .email(EXISTED_EMAIL)
                .password(EXISTED_PASSWORD)
                .build();

        deletedAccount = Account.builder()
                .email(DELETED_EMAIL)
                .password(EXISTED_PASSWORD)
                .deleted(true)
                .build();

        sessionCreateDto = SessionCreateDto.builder()
                .email(EXISTED_EMAIL)
                .password(EXISTED_PASSWORD)
                .build();

        NotExistedEmailDto = SessionCreateDto.builder()
                .email(NOT_EXISTED_EMAIL)
                .password(EXISTED_PASSWORD)
                .build();

        NotExistedPasswordDto = SessionCreateDto.builder()
                .email(EXISTED_EMAIL)
                .password(NOT_EXISTED_PASSWORD)
                .build();

        DeletedEmailDto = SessionCreateDto.builder()
                .email(DELETED_EMAIL)
                .password(EXISTED_PASSWORD)
                .build();

        sessionResultDto = SessionResultDto.builder()
                .accessToken(EXISTED_ACCESSTOKEN)
                .build();

        userRole = Role.builder()
                .email(EXISTED_EMAIL)
                .name("USER")
                .build();
    }

    @Test
    void authenticateUserWithValidAttribute() {
        given(accountRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.of(setUpAccount));

        Account account = authenticationService.authenticateUser(sessionCreateDto);

        assertThat(account.getEmail()).isEqualTo(EXISTED_EMAIL);
    }

    @Test
    void authenticateUserWithNotExistedEmail() {
        given(accountRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticateUser(NotExistedPasswordDto))
                .isInstanceOf(AuthenticationBadRequestException.class);
    }

    @Test
    void authenticateUserWithNotExistedPassword() {
        given(accountRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.of(setUpAccount));

        assertThatThrownBy(() -> authenticationService.authenticateUser(NotExistedEmailDto))
                .isInstanceOf(AuthenticationBadRequestException.class);
    }

    @Test
    void authenticateUserWithDeletedEmail() {
        given(accountRepository.findByEmail(DELETED_EMAIL)).willReturn(Optional.of(deletedAccount));

        assertThatThrownBy(() -> authenticationService.authenticateUser(DeletedEmailDto))
                .isInstanceOf(AuthenticationBadRequestException.class);
    }

    @Test
    void createTokenWithValidUser() {
        given(accountRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.of(setUpAccount));

        SessionResultDto token = authenticationService.createToken(sessionCreateDto);

        assertThat(token.getAccessToken()).contains(".");
    }

    @Test
    void parseTokenWithValidToken() {
        ParseResultDto parseResultDto = authenticationService.parseToken(TOKEN);
        Claims claims = parseResultDto.getClaims();

        assertThat(claims.getSubject()).isEqualTo(EXISTED_EMAIL);
        assertThat(claims.get("userId", Long.class)).isEqualTo(EXISTED_ID);
    }

    @Test
    void parseTokenWithNull() {
        assertThatThrownBy(() -> authenticationService.parseToken(null))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void parseWithInvalidToken() {
        assertThatThrownBy(() -> authenticationService.parseToken(INVALID_TOKEN))
                .isInstanceOf(InvalidTokenException.class);
    }

    @Test
    void detailRoleOfUser() {
        given(roleRepository.findAllByEmail(EXISTED_EMAIL)).willReturn(List.of(userRole));

        List<Role> lists = authenticationService.roles(EXISTED_EMAIL);

        assertThat(lists.get(0).getName()).isEqualTo(("USER"));
    }
}
