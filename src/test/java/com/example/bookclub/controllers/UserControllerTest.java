package com.example.bookclub.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    private static final Long EXISTED_ID = 1L;

    @Autowired
    MockMvc mockMvc;

    @Test
    void detailWithExistedId() throws Exception {
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
}
