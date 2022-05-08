package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.application.EmailService;
import com.example.bookclub.application.LoginService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.dto.EmailSendResultDto;
import com.example.bookclub.dto.KakaoLoginRequest;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import com.example.bookclub.security.UserAccount;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.util.List;

import static com.example.bookclub.utils.ApiDocumentUtils.getDocumentRequest;
import static com.example.bookclub.utils.ApiDocumentUtils.getDocumentResponse;
import static com.fasterxml.jackson.databind.node.JsonNodeType.STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginApiController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith({RestDocumentationExtension.class})
public class LoginApiControllerTest {
	private final static String KAKAO_LOGIN_EMAIL = "kakaoLoginEmail";
	private final static String KAKAO_LOGIN_EMAIL_NOT_EXISTED = "kakaoLoginEmailNotExisted";
	private final static String AUTHENTICATION_NUMBER = "authenticationNumber";

	private KakaoLoginRequest kakaoLoginRequest;
	private KakaoLoginRequest kakaoLoginNotExistedEmailRequest;
	private Account account;
	private UsernamePasswordAuthenticationToken accountToken;
	private EmailSendResultDto emailSendResultDto;

	@MockBean
	private LoginService loginService;

	@MockBean
	private EmailService emailService;

	@MockBean
	private DataSource dataSource;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean
	private AccountAuthenticationService accountAuthenticationService;

	@MockBean
	private CustomEntryPoint customEntryPoint;

	@MockBean
	private CustomDeniedHandler customDeniedHandler;

	@MockBean
	private PersistTokenRepository tokenRepository;

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.apply(documentationConfiguration(restDocumentationContextProvider))
				.alwaysDo(print())
				.build();

		kakaoLoginRequest = KakaoLoginRequest.builder()
				.email(KAKAO_LOGIN_EMAIL)
				.build();

		kakaoLoginNotExistedEmailRequest = KakaoLoginRequest.builder()
				.email(KAKAO_LOGIN_EMAIL_NOT_EXISTED)
				.build();

		account = Account.builder()
				.email(KAKAO_LOGIN_EMAIL)
				.build();

		accountToken =
				new UsernamePasswordAuthenticationToken(
						new UserAccount(account, List.of(new SimpleGrantedAuthority("KAKAO-USER"))),
						null,
						List.of(new SimpleGrantedAuthority("KAKAO-USER")));

		emailSendResultDto = EmailSendResultDto.builder()
				.email(KAKAO_LOGIN_EMAIL_NOT_EXISTED)
				.authenticationNumber(AUTHENTICATION_NUMBER)
				.build();
	}

	@Test
	void loginKakaoWithAlreadyExistedEmail() throws Exception {
		given(loginService.checkAlreadyExistedEmail(any(KakaoLoginRequest.class))).willReturn(true);
		given(loginService.makeKakaoAuthenticationToken(any(KakaoLoginRequest.class))).willReturn(accountToken);

		mockMvc.perform(
				RestDocumentationRequestBuilders.post("/api/kakao-login")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(kakaoLoginRequest))
		)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("email").value(KAKAO_LOGIN_EMAIL))
				.andExpect(jsonPath("authenticationNumber").isEmpty())
				.andDo(document("kakao-login-existed-email",
						getDocumentRequest(),
						getDocumentResponse(),
						requestFields
						(
								fieldWithPath("email").type(STRING).description("사용자 이메일")
						),
						responseFields(
								fieldWithPath("email").type(STRING).description("사용자 식별자"),
								fieldWithPath("authenticationNumber").description("사용자 인증번호")
						)
				));
	}

	@Test
	void loginKakaoWithNotExistedEmail() throws Exception {
		given(loginService.checkAlreadyExistedEmail(any(KakaoLoginRequest.class))).willReturn(false);
		given(emailService.saveAuthenticationNumber(any(EmailRequestDto.class))).willReturn(emailSendResultDto);

		mockMvc.perform(
						RestDocumentationRequestBuilders.post("/api/kakao-login")
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(kakaoLoginNotExistedEmailRequest))
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("email").value(KAKAO_LOGIN_EMAIL_NOT_EXISTED))
				.andExpect(jsonPath("authenticationNumber").value(AUTHENTICATION_NUMBER))
				.andDo(document("kakao-login-not-existed-email",
						getDocumentRequest(),
						getDocumentResponse(),
						requestFields
								(
										fieldWithPath("email").type(STRING).description("사용자 이메일")
								),
						responseFields(
								fieldWithPath("email").type(STRING).description("사용자 식별자"),
								fieldWithPath("authenticationNumber").type(STRING).description("사용자 인증번호")
						)
				));
	}
}
