package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.application.EmailService;
import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.dto.EmailSendResultDto;
import com.example.bookclub.errors.EmailBadRequestException;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;

import static com.example.bookclub.utils.ApiDocumentUtils.getDocumentRequest;
import static com.example.bookclub.utils.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailApiController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith({RestDocumentationExtension.class})
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
    EmailSendResultDto emailSendResultDto;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .alwaysDo(print())
                .build();

        emailRequestDto = EmailRequestDto.builder()
                .email(VALID_EMAIL)
                .build();

        emailBadRequestDto = EmailRequestDto.builder()
                .email(INVALID_EMAIL)
                .build();

        emailSendResultDto = EmailSendResultDto.builder()
                .email(VALID_EMAIL)
                .build();
    }

    @Test
    void sendAuthenticationNumberWithValidEmail() throws Exception {
        given(emailService.sendAuthenticationNumber(any(EmailRequestDto.class))).willReturn(emailSendResultDto);

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/email/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequestDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("email", is(emailSendResultDto.getEmail())))
                .andDo(document("email-authenticationNumber-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("email").type(STRING).description("이메일")
                        )
                ));
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
