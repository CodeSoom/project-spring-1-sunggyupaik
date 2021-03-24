package com.example.bookclub.controllers;

import com.example.bookclub.application.UserService;
import com.example.bookclub.domain.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
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

    private User setUpUser;
    private User createUserData;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        setUpUser = User.builder()
            .id(EXISTED_ID)
            .name(SETUP_NAME)
            .email(SETUP_EMAIL)
            .nickname(SETUP_NICKNAME)
            .password(SETUP_PASSWORD)
            .profileImage(SETUP_PROFILEIMAGE)
            .build();

        createUserData = User.builder()
            .name(CREATED_NAME)
            .email(CREATED_EMAIL)
            .nickname(CREATED_NICKNAME)
            .password(CREATED_PASSWORD)
            .profileImage(CREATED_PROFILEIMAGE)
            .build();
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    UserService userService;

    @Test
    void detailWithExistedId() throws Exception {
        given(userService.getUser(EXISTED_ID)).willReturn(setUpUser);

        mockMvc.perform(
                get("/users/{id}", EXISTED_ID)
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("홍길동"))
                .andExpect(jsonPath("email").value("abcd@naver.com"))
                .andExpect(jsonPath("nickname").value("abcd"))
                .andExpect(jsonPath("password").value("1234"))
                .andExpect(jsonPath("profileImage").value("image"))
                .andExpect(jsonPath("deleted").value(false));
    }

    @Test
    void createWithValidAttribute() throws Exception {
        given(userService.createUser(any(User.class))).will(invocation -> {
            User user = invocation.getArgument(0);
            return User.builder()
                    .id(CREATED_ID)
                    .name(user.getName())
                    .email(user.getEmail())
                    .nickname(user.getNickname())
                    .password(user.getPassword())
                    .profileImage(user.getProfileImage())
                    .build();
        });

        mockMvc.perform(
                post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createUserData))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(CREATED_ID))
                .andExpect(jsonPath("name").value(CREATED_NAME))
                .andExpect(jsonPath("email").value(CREATED_EMAIL))
                .andExpect(jsonPath("nickname").value(CREATED_NICKNAME))
                .andExpect(jsonPath("password").value(CREATED_PASSWORD))
                .andExpect(jsonPath("profileImage").value(CREATED_PROFILEIMAGE))
                .andExpect(jsonPath("deleted").value(false));
    }
}
