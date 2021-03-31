package com.example.bookclub.application;

import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {
    private static final Long EXISTED_ID = 1L;
    private static final String EXISTED_EMAIL = "setUpEmail";
    private static final String EXISTED_PASSWORD = "12345678";

    private static final String NOT_EXISTED_EMAIL = "invalidEmail";
    private static final String NOT_EXISTED_PASSWORD = "invalidPassword";

    private UserRepository userRepository;
    private AuthenticationService authenticationService;

    private User setUpUser;
    private SessionCreateDto sessionCreateDto;
    private SessionCreateDto NotExistedEmailDto;
    private SessionCreateDto NotExistedPasswordDto;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        authenticationService = new AuthenticationService(userRepository);

        setUpUser = User.builder()
                .email(EXISTED_EMAIL)
                .password(EXISTED_PASSWORD)
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
}
