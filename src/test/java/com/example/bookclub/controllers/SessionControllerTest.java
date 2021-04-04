package com.example.bookclub.controllers;

import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SessionController.class)
class SessionControllerTest {
    private static final String EXISTED_EMAIL = "setupEmail";
    private static final String EXISTED_PASSWORD = "1234";
    private static final String EXISTED_ACCESSTOKEN = "12345678";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    private SessionCreateDto sessionCreateDto;
    private SessionResultDto sessionResultDto;

    @BeforeEach
    void setUp() {
        sessionCreateDto = SessionCreateDto.builder()
                .email(EXISTED_EMAIL)
                .password(EXISTED_PASSWORD)
                .build();

        sessionResultDto = SessionResultDto.builder()
                .accessToken(EXISTED_ACCESSTOKEN)
                .build();
    }

    @Test
    void createdTokenWithExistedUser() throws Exception {
        mockMvc.perform(
                post("/session")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sessionCreateDto))
        )
            .andDo(print())
            .andExpect(status().isCreated())
                .andExpect(content().string(StringContains.containsString(".")));
    }
}
