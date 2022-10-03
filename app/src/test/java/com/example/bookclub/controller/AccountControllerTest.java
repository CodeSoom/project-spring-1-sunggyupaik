package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.uploadfile.UploadFileService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.infrastructure.study.JpaStudyRepository;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
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

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
	private static final String EMAIL = "1234@naver.com";
	private Account account;

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
}
