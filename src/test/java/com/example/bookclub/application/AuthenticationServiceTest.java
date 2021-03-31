package com.example.bookclub.application;

import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import com.example.bookclub.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {
    private static final String SECRET = "12345678901234567890123456789010";
    private static final Long EXISTED_ID = 1L;
    private static final String EXISTED_EMAIL = "setUpEmail";
    private static final String EXISTED_PASSWORD = "12345678";
    private static final String DELETED_EMAIL = "deletedEmail";
    private static final String EXISTED_ACCESSTOKEN = "setUpaccessToken";

    private static final String NOT_EXISTED_EMAIL = "invalidEmail";
    private static final String NOT_EXISTED_PASSWORD = "invalidPassword";

    private User setUpUser;
    private User deletedUser;
    private SessionCreateDto sessionCreateDto;
    private SessionCreateDto NotExistedEmailDto;
    private SessionCreateDto NotExistedPasswordDto;
    private SessionCreateDto DeletedEmailDto;

    private SessionResultDto sessionResultDto;


    private UserRepository userRepository;
    private JwtUtil jwtUtil;
    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtUtil = new JwtUtil(SECRET);
        authenticationService = new AuthenticationService(userRepository, jwtUtil);

        setUpUser = User.builder()
                .id(EXISTED_ID)
                .email(EXISTED_EMAIL)
                .password(EXISTED_PASSWORD)
                .build();

        deletedUser = User.builder()
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
    }

    @Test
    void authenticateUserWithValidAttribute() {
        given(userRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.of(setUpUser));

        User user = authenticationService.authenticateUser(sessionCreateDto);

        assertThat(user.getEmail()).isEqualTo(EXISTED_EMAIL);
    }

    @Test
    void authenticateUserWithNotExistedEmail() {
        given(userRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> authenticationService.authenticateUser(NotExistedPasswordDto))
                .isInstanceOf(AuthenticationBadRequestException.class);
    }

    @Test
    void authenticateUserWithNotExistedPassword() {
        given(userRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.of(setUpUser));

        assertThatThrownBy(() -> authenticationService.authenticateUser(NotExistedEmailDto))
                .isInstanceOf(AuthenticationBadRequestException.class);
    }

    @Test
    void authenticateUserWithDeletedEmail() {
        given(userRepository.findByEmail(DELETED_EMAIL)).willReturn(Optional.of(deletedUser));

        assertThatThrownBy(() -> authenticationService.authenticateUser(DeletedEmailDto))
                .isInstanceOf(AuthenticationBadRequestException.class);
    }

    @Test
    void createTokenWithValidUser() {
        given(userRepository.findByEmail(EXISTED_EMAIL)).willReturn(Optional.of(setUpUser));

        SessionResultDto token = authenticationService.createToken(sessionCreateDto);

        assertThat(token.getAccessToken()).contains(".");
    }
}
