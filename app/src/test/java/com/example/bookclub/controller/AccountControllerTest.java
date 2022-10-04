package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.study.query.StudyQueryService;
import com.example.bookclub.application.uploadfile.UploadFileService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.infrastructure.study.JpaStudyRepository;
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

import static org.hamcrest.Matchers.containsString;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
	private static final String EMAIL = "1234@naver.com";

	private Account account;
	private UserAccount userAccount;
	private UsernamePasswordAuthenticationToken accountToken;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@MockBean
	private StudyQueryService studyQueryService;

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
	private UploadFileService uploadFileService;

	@MockBean
	private JpaStudyRepository studyRepository;

	@BeforeEach
	void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.build();

		account = Account.builder()
				.id(1L)
				.build();

		List<GrantedAuthority> USER_AUTHORITY = new ArrayList<GrantedAuthority>();
		USER_AUTHORITY.add(new SimpleGrantedAuthority("USER"));

		List<GrantedAuthority> USER_ANONYMOUS = new ArrayList<GrantedAuthority>();
		USER_AUTHORITY.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));

		userAccount = UserAccount.builder()
				.account(account)
				.authorities(USER_AUTHORITY)
				.build();

		accountToken = new UsernamePasswordAuthenticationToken(
				userAccount, null, USER_AUTHORITY
		);
	}

	@Nested
	@DisplayName("accountSave 메서드는")
	class Describe_accountSave{
		@Nested
		@DisplayName("만약 로그인한 사용자가 주어진다면")
		class Context_WithAccount {
			@Test
			@DisplayName("메인 화면을 리턴한다")
			void itReturnsMainView() throws Exception {
				mockMvc.perform(get("/users/save")
								.param("account", objectMapper.writeValueAsString(account))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.valueOf("text/html;charset=UTF-8")))
						.andExpect(view().name("users/users-save"));
			}
		}

		@Nested
		@DisplayName("만약 로그인한 사용자가 주어지지 않는다면")
		class Context_WithoutAccount {
			@Test
			@DisplayName("회원가입 페이지를 리턴한다")
			void itReturnsUsersSaveView() throws Exception {
				mockMvc.perform(get("/users/save")
						)
						.andExpect(status().isOk())
						.andExpect(content().contentType(MediaType.valueOf("text/html;charset=UTF-8")))
						.andExpect(view().name("users/users-save"));
			}
		}
	}

	@Nested
	@DisplayName("accountFavorite 메서드는")
	class Describe_accountFavorite {
		@Nested
		@DisplayName("만약 로그인한 사용자가 주어진다면")
		class Context_WithAccount {
			@Test
			@DisplayName("즐겨찾기 화면을 리턴한다")
			void itReturnsUsersFavoriteView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/users/{id}/favorite", 1L)
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(model().attributeExists("StudyFavoriteDto"))
						.andExpect(view().name("users/users-favorite"));
			}
		}

		@Nested
		@DisplayName("만약 로그인한 사용자가 주어지지 않는다면")
		class Context_WithoutAccount {
			@Test
			@DisplayName("로그인이 필요하다는 오류메세지를 리턴한다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsFailedToEvaluateExpressionMessage() throws Exception {
				mockMvc.perform(get("/users/{id}/favorite", 1L))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(content().string(containsString("Failed to evaluate expression")))
						.andExpect(status().is5xxServerError());
			}
		}

		@Nested
		@DisplayName("만약 로그인한 사용자와 이동하려는 즐겨찾기 페이지에 다른 식별자가 주어진다면")
		class Context_WithNotMineFavoriteAccountId {
			private final Long FAVORITE_ID = 3L;

			@Test
			@DisplayName("접근이 불가능하다는 오류메세지를 리턴한다")
			void itReturnsAccessIsDeniedMessage() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/users/{id}/favorite", FAVORITE_ID)
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(content().string(containsString("Access is denied")))
						.andExpect(status().is5xxServerError());
			}
		}
	}
}
