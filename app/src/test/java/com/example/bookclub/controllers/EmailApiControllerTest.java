package com.example.bookclub.controllers;

import com.example.bookclub.dto.EmailRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailApiController.class)
class EmailApiControllerTest {
    private static final String VALID_EMAIL = "abcd@naver.com";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void sendAuthenticationNumberWithValidEmail() throws Exception {
        EmailRequestDto emailRequestDto = EmailRequestDto.builder()
                .email(VALID_EMAIL)
                .build();

        mockMvc.perform(
                post("/api/email/authentication")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emailRequestDto))
        )
                .andDo(print())
                .andExpect(status().isOk());
    }
}
