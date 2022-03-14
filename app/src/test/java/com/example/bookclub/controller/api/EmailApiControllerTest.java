package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.application.EmailService;
import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.errors.EmailBadRequestException;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
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

import javax.sql.DataSource;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailApiController.class)
class EmailApiControllerTest {
    private static final String VALID_EMAIL = "validEmail";
    private static final String INVALID_EMAIL = "invalidEmail";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private AccountAuthenticationService accountAuthenticationService;

    @MockBean
    private DataSource dataSource;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomEntryPoint customEntryPoint;

    @MockBean
    private CustomDeniedHandler customDeniedHandler;

    @MockBean
    private PersistTokenRepository tokenRepository;

    @MockBean
    private EmailService emailService;

    EmailRequestDto emailRequestDto;
    EmailRequestDto emailBadRequestDto;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        emailRequestDto = EmailRequestDto.builder()
                .email(VALID_EMAIL)
                .build();

        emailBadRequestDto = EmailRequestDto.builder()
                .email(INVALID_EMAIL)
                .build();
    }

    @Test
    void sendAuthenticationNumberWithValidEmail() throws Exception {
        given(emailService.sendAuthenticationNumber(eq(emailRequestDto))).willReturn(VALID_EMAIL);

        mockMvc.perform(
                post("/api/email/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequestDto))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void sendAuthenticationNumberWithInValidEmail() throws Exception {
        given(emailService.sendAuthenticationNumber(any(EmailRequestDto.class)))
                .willThrow(EmailBadRequestException.class);

        mockMvc.perform(
                        post("/api/email/authentication")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailBadRequestDto))
                )
                .andDo(print())
//                .andExpect(content().string(containsString("Email bad request")))
                .andExpect(status().isBadRequest());
    }
}
