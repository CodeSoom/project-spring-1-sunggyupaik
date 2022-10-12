package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.post.PostService;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(PostController.class)
class PostControllerTest {
	private static final Long ACCOUNT_EXISTED_ID = 1L;
	private static final String ACCOUNT_EMAIL = "1234@naver.com";
	private static final String ACCOUNT_NAME = "paik";
	private static final String ACCOUNT_NICKNAME ="bluesky";

	private Account account;
	private Study study;
	private UserAccount userAccount;
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
	private PostService postService;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.build();

		account = Account.builder()
				.id(ACCOUNT_EXISTED_ID)
				.email(ACCOUNT_EMAIL)
				.name(ACCOUNT_NAME)
				.nickname(ACCOUNT_NICKNAME)
				.build();

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
	@DisplayName("post 메서드는")
	class Describe_post {
		@Nested
		@DisplayName("검색어가 주어지면")
		class Context_WithContent {
			@Test
			@DisplayName("검색어에 해당하는 내용을 가진 한줄 게시판 화면을 리턴한다")
			void itReturnsPostsView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/posts"))
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("posts/posts")
						);
			}
		}
	}
}