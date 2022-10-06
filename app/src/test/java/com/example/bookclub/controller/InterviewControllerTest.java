package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.interview.InterviewService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.InterviewDto;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(InterviewController.class)
public class InterviewControllerTest {
	private static final Long EXISTED_ID = 1L;
	private static final String EMAIL = "1234@naver.com";
	private static final String NAME = "paik";
	private static final String NICKNAME ="bluesky";

	private Account account;
	private UserAccount userAccount;

	private Page<InterviewDto.InterviewResultDto> page;
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
	private InterviewService interviewService;

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

		page = Page.empty();

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
	@DisplayName("interviewLists 메서드는")
	class Describe_interviewLists {
		@Nested
		@DisplayName("로그인한 사용자가 주어진다면")
		class Context_WithAccount {
			private final Long EXISTED_ID = 1L;

			@Test
			@DisplayName("인터뷰 조회 화면을 리턴한다")
			void itReturnsInterviewsListView() throws Exception {
				given(interviewService.getAllInterviews(any(Pageable.class))).willReturn(page);
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/interviews")
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("interviews/interviews-list"));
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않는다면")
		class Context_WithNotAccount {
			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsNull() throws Exception {
				mockMvc.perform(get("/interviews"))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}
	}
}
