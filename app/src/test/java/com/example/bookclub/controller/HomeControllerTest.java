package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.study.StudyService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import com.example.bookclub.security.UserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(HomeController.class)
class HomeControllerTest {
	private static final Long EXISTED_ID = 1L;
	private static final String EMAIL = "1234@naver.com";
	private static final String NAME = "paik";
	private static final String NICKNAME ="bluesky";

	private Account account;
	private UserAccount userAccount;

	private Study study;
	private UsernamePasswordAuthenticationToken accountToken;

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
	private PersistTokenRepository persistTokenRepository;

	@MockBean
	private AccountService accountService;

	@MockBean
	private StudyService studyService;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.build();

		account = Account.builder()
				.id(EXISTED_ID)
				.email(EMAIL)
				.name(NAME)
				.nickname(NICKNAME)
				.build();

		study = Study.builder().id(1L).build();

		account.addStudy(study);

		List<GrantedAuthority> ROLE_USER = new ArrayList<GrantedAuthority>();
		ROLE_USER.add(new SimpleGrantedAuthority("USER"));

		userAccount = UserAccount.builder()
				.account(account)
				.authorities(ROLE_USER)
				.build();

		accountToken = new UsernamePasswordAuthenticationToken(
				userAccount, null, ROLE_USER
		);
	}

	@Nested
	@DisplayName("home 메서드는")
	class Describe_home {
		@Nested
		@DisplayName("로그인한 사용자가 주어지면")
		class Context_WithAccount {
			@Test
			@DisplayName("즐겨찾기, 참여중 스터디 버튼과 홈 화면을 리턴한다")
			void itReturnsIndexView() throws Exception {
				given(accountAuthenticationService.getAccountByEmail(EMAIL)).willReturn(account);
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/")
								.param("account", objectMapper.writeValueAsString(account))
						)
						.andExpect(status().isOk())
						.andExpect(model().attributeExists("account"))
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("index"));
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않으면")
		class Context_WithNotAccount {
			@Test
			@DisplayName("회원가입 버튼과 홈 화면을 리턴한다")
			void itReturnsIndexView() throws Exception {

				mockMvc.perform(get("/"))
						.andExpect(status().isOk())
						.andExpect(model().attributeDoesNotExist("account"))
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("index"));
			}
		}
	}
}
