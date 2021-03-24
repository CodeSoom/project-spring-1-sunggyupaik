package com.example.bookclub.controllers;

import com.example.bookclub.application.UserService;
import com.example.bookclub.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

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

    private User setUpUserOne;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        setUpUserOne = User.builder()
            .id(EXISTED_ID)
            .name(SETUP_NAME)
            .email(SETUP_EMAIL)
            .nickname(SETUP_NICKNAME)
            .password(SETUP_PASSWORD)
            .profileImage(SETUP_PROFILEIMAGE)
            .build();
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    UserService userService;

    @Test
    void detailWithExistedId() throws Exception {
        given(userService.getUser(EXISTED_ID)).willReturn(setUpUserOne);

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
                .andExpect(jsonPath("profileImage").value("image"));
    }

    @Test
    void createWithValidAttribute() throws Exception {
        mockMvc.perform(
                post("/users")
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(1L))
                .andExpect(jsonPath("name").value("홍길동"))
                .andExpect(jsonPath("email").value("abcd@naver.com"))
                .andExpect(jsonPath("nickname").value("abcd"))
                .andExpect(jsonPath("password").value("1234"))
                .andExpect(jsonPath("profileImage").value("image"));
    }
}
