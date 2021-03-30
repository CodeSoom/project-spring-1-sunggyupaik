package com.example.bookclub.application;

import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.UserCreateDto;
import com.example.bookclub.dto.UserResultDto;
import com.example.bookclub.dto.UserUpdateDto;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
import com.example.bookclub.errors.UserEmailDuplicatedException;
import com.example.bookclub.errors.UserNicknameDuplicatedException;
import com.example.bookclub.errors.UserPasswordBadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

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

    private static final String UPDATED_NICKNAME = "qwer";
    private static final String NOT_EXISTED_PASSWORD = "5678";
    private static final String UPDATED_PASSWORD = "1234";

    private static final String UPDATED_PROFILEIMAGE = "picture";

    private static final String EXISTED_EMAIL = "abcd@naver.com";
    private static final String EXISTED_NICKNAME = "abcd";
    private static final String EXISTED_AUTHENTICATIONNUMBER = "12345";
    private static final String NOT_EXISTED_AUTHENTICATIONNUMBER = "67890";

    private User setUpUser;
    private User createdUser;

    private UserCreateDto userCreateDto;
    private UserCreateDto emailExistedUserCreateDto;
    private UserCreateDto nicknameExistedUserCreateDto;
    private UserUpdateDto userUpdateDto;
    private UserUpdateDto nicknameExistedUserUpdateDto;
    private UserUpdateDto passwordNotExistedUserUpdateDto;
    private EmailAuthentication emailAuthentication;

    private UserService userService;
    private UserRepository userRepository;
    private EmailAuthenticationRepository emailAuthenticationRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        emailAuthenticationRepository = mock(EmailAuthenticationRepository.class);
        userService = new UserService(userRepository, emailAuthenticationRepository);

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
                .authenticationNumber(EXISTED_AUTHENTICATIONNUMBER)
                .build();

        userUpdateDto = UserUpdateDto.builder()
                .nickname(UPDATED_NICKNAME)
                .password(UPDATED_PASSWORD)
                .profileImage(UPDATED_PROFILEIMAGE)
                .build();

        emailExistedUserCreateDto = UserCreateDto.builder()
                .name(CREATED_NAME)
                .email(EXISTED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        nicknameExistedUserCreateDto = UserCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(EXISTED_NICKNAME)
                .password(CREATED_PASSWORD)
                .profileImage(CREATED_PROFILEIMAGE)
                .build();

        nicknameExistedUserUpdateDto = UserUpdateDto.builder()
                .nickname(SETUP_NICKNAME)
                .password(SETUP_PASSWORD)
                .profileImage(UPDATED_PROFILEIMAGE)
                .build();

        passwordNotExistedUserUpdateDto = UserUpdateDto.builder()
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
        given(userRepository.save(any(User.class))).willReturn(createdUser);
        given(emailAuthenticationRepository.findByEmail(CREATED_EMAIL))
                .willReturn(Optional.of(emailAuthentication));

        UserResultDto userResultDto = userService.createUser(userCreateDto);

        assertThat(userResultDto.getId()).isEqualTo(CREATED_ID);
        assertThat(userResultDto.getName()).isEqualTo(userCreateDto.getName());
        assertThat(userResultDto.getEmail()).isEqualTo(userCreateDto.getEmail());
        assertThat(userResultDto.getNickname()).isEqualTo(userCreateDto.getNickname());
        assertThat(userResultDto.getPassword()).isEqualTo(userCreateDto.getPassword());
        assertThat(userResultDto.getProfileImage()).isEqualTo(userCreateDto.getProfileImage());

        verify(emailAuthenticationRepository).delete(emailAuthentication);
    }

    @Test
    public void createWithDuplicatedEmail() {
        given(userRepository.existsByEmail(EXISTED_EMAIL)).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(emailExistedUserCreateDto))
                .isInstanceOf(UserEmailDuplicatedException.class);
    }

    @Test
    public void createWithDuplicatedNickname() {
        given(userRepository.existsByNickname(EXISTED_NICKNAME)).willReturn(true);

        assertThatThrownBy(() -> userService.createUser(nicknameExistedUserCreateDto))
                .isInstanceOf(UserNicknameDuplicatedException.class);
    }

    @Test
    public void createWithNotValidAuthenticationNumber() {
        given(emailAuthenticationRepository.findByEmail(SETUP_EMAIL)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.createUser(userCreateDto))
                .isInstanceOf(EmailNotAuthenticatedException.class);
    }

    @Test
    public void updateWithValidAttribute() {
        given(userRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpUser));

        UserResultDto userResultDto = userService.updateUser(EXISTED_ID, userUpdateDto);
        
        assertThat(userResultDto.getNickname()).isEqualTo(userUpdateDto.getNickname());
        assertThat(userResultDto.getPassword()).isEqualTo(userUpdateDto.getPassword());
        assertThat(userResultDto.getProfileImage()).isEqualTo(userUpdateDto.getProfileImage());
    }

    @Test
    public void updatedWithExistedNickname() {
        given(userRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpUser));

        assertThatThrownBy(() -> userService.updateUser(EXISTED_ID, nicknameExistedUserUpdateDto))
                .isInstanceOf(UserNicknameDuplicatedException.class);
    }

    @Test
    public void updateWithNotValidPassword() {
        given(userRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpUser));

        assertThatThrownBy(() -> userService.updateUser(EXISTED_ID, passwordNotExistedUserUpdateDto))
                .isInstanceOf(UserPasswordBadRequestException.class);
    }
}
