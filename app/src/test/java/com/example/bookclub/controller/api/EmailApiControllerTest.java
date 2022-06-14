package com.example.bookclub.controller.api;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.EmailService;
import com.example.bookclub.dto.EmailDto;
import com.example.bookclub.common.exception.account.emailauthentication.EmailBadRequestException;
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

import static com.example.bookclub.common.util.ApiDocumentUtils.getDocumentRequest;
import static com.example.bookclub.common.util.ApiDocumentUtils.getDocumentResponse;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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

    EmailDto.EmailRequestDto emailRequestDto;
    EmailDto.EmailRequestDto emailBadRequestDto;
    EmailDto.EmailSendResultDto emailSendResultDto;

    @BeforeEach
    void setup(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDocumentationContextProvider))
                .alwaysDo(print())
                .build();

        emailRequestDto = EmailDto.EmailRequestDto.builder()
                .email(VALID_EMAIL)
                .build();

        emailBadRequestDto = EmailDto.EmailRequestDto.builder()
                .email(INVALID_EMAIL)
                .build();

        emailSendResultDto = EmailDto.EmailSendResultDto.builder()
                .email(VALID_EMAIL)
                .build();
    }

    @Test
    void sendAuthenticationNumberWithValidEmail() throws Exception {
        given(emailService.sendAuthenticationNumber(any(EmailDto.EmailRequestDto.class))).willReturn(emailSendResultDto);

        this.mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/email/authentication")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailRequestDto))
                )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.email", is(emailSendResultDto.getEmail())))
                .andDo(document("email-authenticationNumber-create",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("data.email").type(STRING).description("이메일"),
                                fieldWithPath("data.authenticationNumber").description("인증번호"),
                                fieldWithPath("message").description("예외 메세지"),
                                fieldWithPath("errorCode").description("에러코드")
                        )
                ));
    }

    @Test
    void sendAuthenticationNumberWithInValidEmail() throws Exception {
        given(emailService.sendAuthenticationNumber(any(EmailDto.EmailRequestDto.class)))
                .willThrow(new EmailBadRequestException(INVALID_EMAIL));

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/email/authentication")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(emailBadRequestDto))
                )
                .andDo(print())
//                .andExpect(content().string(containsString("Email bad request")))
                .andExpect(status().isBadRequest())
                .andDo(document("email-authenticationNumber-create-invalid",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                fieldWithPath("email").type(STRING).description("이메일")
                        ),
                        responseFields(
                                fieldWithPath("message").type(STRING).description("예외 메세지"),
                                fieldWithPath("result").type(STRING).description("결과"),
                                fieldWithPath("errorCode").type(STRING).description("에러코드"),
                                fieldWithPath("data").description("데이터")
                        )
                ));
    }
}
