package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.interview.BookService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.BookType;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(BookController.class)
class BookControllerTest {
	private static final Long EXISTED_ID = 1L;
	private static final String EMAIL = "1234@naver.com";
	private static final String NAME = "paik";
	private static final String NICKNAME ="bluesky";

	private Account account;
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
	private BookService bookService;

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
	@DisplayName("booksBestSellerLists 메서드는")
	class Describe_booksBestSellerLists {
		@Nested
		@DisplayName("로그인한 사용자가 주어지면")
		class Context_WithAccount {
			@Test
			@DisplayName("베스트셀러 도서 조회 페이지를 리턴한다")
			void itReturnsBookListsView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/books/bestseller")
								.param("account", objectMapper.writeValueAsString(account))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(model().attribute("bookType", BookType.BESTSELLER.getTitle()))
						.andExpect(view().name("books/books-lists"));
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않으면")
		class Context_WithNotAccount {
			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsNull() throws Exception {
				mockMvc.perform(get("/books/bestseller"))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}
	}

	@Nested
	@DisplayName("booksRecommendLists 메서드는")
	class Describe_booksRecommendLists {
		@Nested
		@DisplayName("로그인한 사용자가 주어지면")
		class Context_WithAccount {
			@Test
			@DisplayName("추천 도서 조회 페이지를 리턴한다")
			void itReturnsBookListsView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/books/recommend")
								.param("account", objectMapper.writeValueAsString(account))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(model().attribute("bookType", BookType.RECOMMEND.getTitle()))
						.andExpect(view().name("books/books-lists"));
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않으면")
		class Context_WithNotAccount {
			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsNull() throws Exception {
				mockMvc.perform(get("/books/recommend"))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}
	}

	@Nested
	@DisplayName("booksNewLists 메서드는")
	class Describe_booksNewLists {
		@Nested
		@DisplayName("로그인한 사용자가 주어지면")
		class Context_WithAccount {
			@Test
			@DisplayName("신간 도서 조회 페이지를 리턴한다")
			void itReturnsBookListsView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/books/new")
								.param("account", objectMapper.writeValueAsString(account))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(model().attribute("bookType", BookType.NEW.getTitle()))
						.andExpect(view().name("books/books-lists"));
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않으면")
		class Context_WithNotAccount {
			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsNull() throws Exception {
				mockMvc.perform(get("/books/new"))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}
	}
}
