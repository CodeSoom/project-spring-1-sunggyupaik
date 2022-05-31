package com.example.bookclub.controller.api;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.interview.InterviewService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.interview.Interview;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.PersistTokenRepository;
import com.example.bookclub.security.UserAccount;
import org.hamcrest.core.StringContains;
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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.example.bookclub.common.utils.ApiDocumentUtils.getDocumentRequest;
import static com.example.bookclub.common.utils.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InterviewApiController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith({RestDocumentationExtension.class})
class InterviewApiControllerTest {
	private final static Long FIRST_ID = 1L;
	private final static String FIRST_INTERVIEW_URL = "firstInterviewUrl";
	private final static String FIRST_IMG_URL = "firstImgUrl";
	private final static String FIRST_AUTHOR = "firstAuthor";
	private final static String FIRST_TITLE = "firstTitle";
	private final static LocalDate FIRST_DATE = LocalDate.now();
	private final static String FIRST_CONTENT = "firstContent";
	private final static LocalDateTime FIRST_CREATED_DATE = LocalDateTime.now();
	private final static LocalDateTime FIRST_UPDATED_DATE = LocalDateTime.now();

	private final static Long SECOND_ID = 2L;
	private final static String SECOND_INTERVIEW_URL = "secondInterviewUrl";
	private final static String SECOND_IMG_URL = "secondImgUrl";
	private final static String SECOND_AUTHOR = "secondAuthor";
	private final static String SECOND_TITLE = "secondTitle";
	private final static LocalDate SECOND_DATE = LocalDate.now();
	private final static String SECOND_CONTENT = "secondContent";
	private final static LocalDateTime SECOND_CREATED_DATE = LocalDateTime.now();
	private final static LocalDateTime SECOND_UPDATED_DATE = LocalDateTime.now();

	private static final Long ACCOUNT_ID = 3L;
	private static final String ACCOUNT_NAME = "accountName";
	private static final String ACCOUNT_EMAIL = "email";
	private static final String ACCOUNT_NICKNAME = "accountNickname";
	private static final String ACCOUNT_PASSWORD = "accountPassword";

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@MockBean
	private AccountAuthenticationService accountAuthenticationService;

	@MockBean
	private DataSource dataSource;

	@MockBean
	private CustomEntryPoint customEntryPoint;

	@MockBean
	private CustomDeniedHandler customDeniedHandler;

	@MockBean
	private PersistTokenRepository tokenRepository;

	@MockBean
	private InterviewService interviewService;

	private List<Interview> interviewList;
	private Interview firstInterview;
	private Interview secondInterview;

	private Account adminAccount;
	private Account userAccount;
	private UsernamePasswordAuthenticationToken adminAccountToken;
	private UsernamePasswordAuthenticationToken userAccountToken;

	@BeforeEach
	void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
				.addFilters(new CharacterEncodingFilter("UTF-8", true))
				.apply(documentationConfiguration(restDocumentationContextProvider))
				.alwaysDo(print())
				.build();

		firstInterview = Interview.builder()
				.id(FIRST_ID)
				.interviewUrl(FIRST_INTERVIEW_URL)
				.imgUrl(FIRST_IMG_URL)
				.author(FIRST_AUTHOR)
				.title(FIRST_TITLE)
				.date(FIRST_DATE)
				.content(FIRST_CONTENT)
				.build();

		firstInterview.setCreatedDate(FIRST_CREATED_DATE);
		firstInterview.setUpdatedDate(FIRST_UPDATED_DATE);

		secondInterview = Interview.builder()
				.id(SECOND_ID)
				.interviewUrl(SECOND_INTERVIEW_URL)
				.imgUrl(SECOND_IMG_URL)
				.author(SECOND_AUTHOR)
				.title(SECOND_TITLE)
				.date(SECOND_DATE)
				.content(SECOND_CONTENT)
				.build();

		secondInterview.setCreatedDate(SECOND_CREATED_DATE);
		secondInterview.setUpdatedDate(SECOND_UPDATED_DATE);

		adminAccount = Account.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.build();

		userAccount = Account.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.build();

		adminAccountToken = new UsernamePasswordAuthenticationToken(
				new UserAccount(adminAccount, List.of(new SimpleGrantedAuthority("ADMIN"))),
				adminAccount.getPassword(),
				List.of(new SimpleGrantedAuthority("ADMIN")));

		userAccountToken = new UsernamePasswordAuthenticationToken(
				new UserAccount(userAccount, List.of(new SimpleGrantedAuthority("USER"))),
				userAccount.getPassword(),
				List.of(new SimpleGrantedAuthority("USER")));

		interviewList = List.of(firstInterview, secondInterview);
	}

	@Test
	void createCrawlAllInterviews() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(adminAccountToken);
		given(interviewService.crawlAllInterviews()).willReturn((interviewList));

		this.mockMvc.perform(
						RestDocumentationRequestBuilders.post("/api/interviews")
						.contentType(MediaType.APPLICATION_JSON)
		)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(content().string(StringContains.containsString("\"imgUrl\":\"" + FIRST_IMG_URL)))
				.andExpect(content().string(StringContains.containsString("\"imgUrl\":\"" + SECOND_IMG_URL)))
				.andDo(document("interview-create",
						getDocumentRequest(),
						getDocumentResponse(),
						responseFields(
								fieldWithPath("[].id").type(NUMBER).description("인터뷰 식별자")	,
								fieldWithPath("[].interviewUrl").type(STRING).description("인터뷰 URL"),
								fieldWithPath("[].imgUrl").type(STRING).description("이미지 URL")	,
								fieldWithPath("[].author").type(STRING).description("작가")	,
								fieldWithPath("[].title").type(STRING).description("제목"),
								fieldWithPath("[].date").type(STRING).description("날짜"),
								fieldWithPath("[].content").type(STRING).description("내용"),
								fieldWithPath("[].createdDate").type(STRING).description("생성날짜"),
								fieldWithPath("[].updatedDate").type(STRING).description("수정날짜")
						)
				));
	}

	@Test
	void createCrawlAllInterviewsWithUserAuthority() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(userAccountToken);
		given(interviewService.crawlAllInterviews()).willThrow(AccessDeniedException.class);

		assertThatThrownBy(
						() -> mockMvc.perform(
									post("/api/interviews")
								)
								.andDo(print())
								.andExpect(status().isCreated())
				)
				.hasCause(new AccessDeniedException("Access is denied"));
	}
}