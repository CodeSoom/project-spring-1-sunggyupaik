package com.example.bookclub.application;

import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.UserCreateDto;
import com.example.bookclub.dto.UserResultDto;
import com.example.bookclub.errors.UserEmailDuplicatedException;
import com.example.bookclub.errors.UserNicknameDuplicatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserServiceTest {
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

    private static final String EXISTED_EMAIL = "abcd@naver.com";
    private static final String EXISTED_NICKNAME = "abcd";
    private static final String EXISTED_AUTHENTICATIONNUMBER = "12345";
    private static final String NOT_EXISTED_AUTHENTICATIONNUMBER = "67890";

    private User setUpUser;
    private User createdUser;

    private UserCreateDto userCreateDto;
    private UserCreateDto emailExistedUserDto;
    private UserCreateDto nicknameExistedUserDto;
    private EmailAuthentication emailAuthentication;

    private UserService userService;
    private UserRepository userRepository;
    private EmailAuthenticationRepository emailAuthenticationRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailAuthenticationRepository = mock(EmailAuthenticationRepository.class);
        userService = new UserService(userRepository);

        setUpUser = User.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .email(SETUP_EMAIL)
                .nickname(SETUP_NICKNAME)
                .password(SETUP_PASSWORD)
                .profileImage(SETUP_PROFILEIMAGE)
                .build();

        createdUser = User.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        userCreateDto = UserCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        emailExistedUserDto = UserCreateDto.builder()
                .name(CREATED_NAME)
                .email(EXISTED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        nicknameExistedUserDto = UserCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(EXISTED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        emailAuthentication = EmailAuthentication.builder()
                .email(SETUP_EMAIL)
                .authenticationNumber(EXISTED_AUTHENTICATIONNUMBER)
                .build();
    }

    @Test
    public void createWithValidAttribute() {
        given(userRepository.save(any(User.class))).willReturn(createdUser);

        UserResultDto userResultDto = userService.createUser(userCreateDto);

        assertThat(userResultDto.getId()).isEqualTo(CREATED_ID);
        assertThat(userResultDto.getName()).isEqualTo(userCreateDto.getName());
        assertThat(userResultDto.getEmail()).isEqualTo(userCreateDto.getEmail());
        assertThat(userResultDto.getNickname()).isEqualTo(userCreateDto.getNickname());
        assertThat(userResultDto.getPassword()).isEqualTo(userCreateDto.getPassword());
        assertThat(userResultDto.getProfileImage()).isEqualTo(userCreateDto.getProfileImage());
    }

    @Test
    public void createWithDuplicatedEmail() {
        given(userRepository.existsByEmail(EXISTED_EMAIL)).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(emailExistedUserDto))
                .isInstanceOf(UserEmailDuplicatedException.class);
    }

    @Test
    public void createWithDuplicatedNickname() {
        given(userRepository.existsByNickname(EXISTED_NICKNAME)).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(nicknameExistedUserDto))
                .isInstanceOf(UserNicknameDuplicatedException.class);
    }

    @Test
    public void createWithNotValidAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(SETUP_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(userCreateDto))
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }
}
