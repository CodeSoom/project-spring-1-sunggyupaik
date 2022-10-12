package com.example.bookclub.controller;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.study.StudyService;
import com.example.bookclub.application.study.query.StudyQueryService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Day;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.domain.study.Zone;
import com.example.bookclub.dto.StudyApiDto;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(StudyController.class)
public class StudyControllerTest {
	private static final Long ACCOUNT_EXISTED_ID = 1L;
	private static final String ACCOUNT_EMAIL = "1234@naver.com";
	private static final String ACCOUNT_NAME = "paik";
	private static final String ACCOUNT_NICKNAME ="bluesky";

	private static final Long ACCOUNT_SECOND_ID = 2L;

	private static final Long STUDY_SETUP_EXISTED_ID = 1L;
	private static final String STUDY_SETUP_NAME = "setupStudyName";
	private static final String STUDY_SETUP_BOOK_NAME = "setupStudyBookName";
	private static final String STUDY_SETUP_BOOK_IMAGE = "setupStudyBookImage";
	private static final String STUDY_SETUP_EMAIL = ACCOUNT_EMAIL;
	private static final String STUDY_SETUP_DESCRIPTION = "setupStudyDescription";
	private static final String STUDY_SETUP_CONTACT = "setupContact";
	private static final int STUDY_SETUP_SIZE = 5;
	private static final LocalDate STUDY_SETUP_START_DATE = LocalDate.now().plusDays(1);
	private static final LocalDate STUDY_SETUP_END_DATE = LocalDate.now().plusDays(7);
	private static final Day STUDY_SETUP_DAY = Day.MONDAY;
	private static final String STUDY_SETUP_START_TIME = "13:00";
	private static final String STUDY_SETUP_END_TIME = "15:30";
	private static final StudyState STUDY_SETUP_STUDY_STATE = StudyState.OPEN;
	private static final Zone STUDY_SETUP_ZONE = Zone.SEOUL;
	private static final String STUDY_SETUP_CREATED_BY = "createdBy";
	private static final String STUDY_SETUP_UPDATED_BY = "updatedBy";
	private static final LocalDateTime STUDY_SETUP_CREATED_DATE = LocalDateTime.now();
	private static final LocalDateTime STUDY_SETUP_UPDATED_DATE = LocalDateTime.now();

	private Account account;
	private Account secondAccount;
	private Study study;
	private Study secondStudy;
	private UserAccount userAccount;

	private StudyApiDto.StudyResultDto studyResultDto;
	private StudyApiDto.StudyDetailResultDto detailedStudy;
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
	private StudyService studyService;

	@MockBean
	private StudyQueryService studyQueryService;

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

		secondAccount = Account.builder()
				.id(ACCOUNT_SECOND_ID)
				.build();

		study = Study.builder()
				.id(STUDY_SETUP_EXISTED_ID)
				.name(STUDY_SETUP_NAME)
				.bookName(STUDY_SETUP_BOOK_NAME)
				.bookImage(STUDY_SETUP_BOOK_IMAGE)
				.email(STUDY_SETUP_EMAIL)
				.description(STUDY_SETUP_DESCRIPTION)
				.contact(STUDY_SETUP_CONTACT)
				.size(STUDY_SETUP_SIZE)
				.startDate(STUDY_SETUP_START_DATE)
				.endDate(STUDY_SETUP_END_DATE)
				.startTime(STUDY_SETUP_START_TIME)
				.endTime(STUDY_SETUP_END_TIME)
				.day(STUDY_SETUP_DAY)
				.studyState(STUDY_SETUP_STUDY_STATE)
				.zone(STUDY_SETUP_ZONE)
				.build();

		study.setCreatedDate(STUDY_SETUP_CREATED_DATE);
		study.setUpdatedDate(STUDY_SETUP_UPDATED_DATE);
		study.setCreatedBy(STUDY_SETUP_CREATED_BY);
		study.setUpdatedBy(STUDY_SETUP_UPDATED_BY);
		study.addAdmin(account);

		account.addStudy(study);
		study.addAccount(account);

		secondStudy = Study.builder()
				.id(2L)
				.email("other@naver.com")
				.build();

		studyResultDto = StudyApiDto.StudyResultDto.of(study);
		detailedStudy = StudyApiDto.StudyDetailResultDto.of(studyResultDto, null);

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
	@DisplayName("studyDetail 메서드는")
	class Describe_studyDetail {
		@Nested
		@DisplayName("로그인한 사용자와 스터디 식별자가 주어진다면")
		class Context_WithAccountAndExistedStudyId {
			private Long EXISTED_ID = 1L;
			@Test
			@DisplayName("스터디 상세 조회 화면을 리턴한다")
			void itReturnsStudiesDetail() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);
				given(studyService.getDetailedStudy(userAccount, EXISTED_ID)).willReturn(detailedStudy);

				mockMvc.perform(get("/studies/{id}", EXISTED_ID)
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("studies/studies-detail")
						);
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않는다면")
		class Context_WithNotAccount {
			private final Long EXISTED_STUDY_ID = 1L;

			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsFailedToEvaluateExpressionMessage() throws Exception {

				mockMvc.perform(get("/studies/{id}", EXISTED_STUDY_ID))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}
	}

	@Nested
	@DisplayName("studySave 메서드는")
	class Describe_studySave {
		@Nested
		@DisplayName("로그인한 사용자가 주어진다면")
		class Context_WithAccount {
			@Test
			@DisplayName("스터디 생성 화면을 리턴한다")
			void itReturnsWithStudiesSaveView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/studies/save")
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("studies/studies-save")
						);
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않는다면")
		class Context_WithNotAccount {
			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsFailedToEvaluateExpressionMessage() throws Exception {

				mockMvc.perform(get("/studies/save"))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}
	}

	@Nested
	@DisplayName("studyUpdate 메서드는")
	class Describe_studyUpdate {
		@Nested
		@DisplayName("로그인한 사용자와 해당 사용자가 만든 스터디 식별자가 주어진다면")
		class Context_WithAccountAndExistedStudyId {
			private final Long EXISTED_STUDY_ID = 1L;

			@Test
			@DisplayName("스터디 수정 화면을 리턴한다")
			void itReturnsStudiesUpdateView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);
				given(studyService.getStudy(EXISTED_STUDY_ID)).willReturn(study);

				mockMvc.perform(get("/studies/update/{id}", EXISTED_STUDY_ID)
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("studies/studies-update")
						);
			}
		}

		@Nested
		@DisplayName("로그인한 사용자가 주어지지 않는다면")
		class Context_WithNotAccount {
			private final Long EXISTED_STUDY_ID = 1L;

			@Test
			@DisplayName("아무런 데이터도 리턴하지 않는다")
			@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
			void itReturnsFailedToEvaluateExpressionMessage() throws Exception {

				mockMvc.perform(get("/studies/update/{id}", EXISTED_STUDY_ID))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().is5xxServerError());
			}
		}

		@Nested
		@DisplayName("로그인한 사용자와 다른 사용자가 방장인 스터디 식별자가 주어진다면")
		class Context_WithAccountAndNotExistedId {
			private final Long NOT_EXISTED_STUDY_ID = 2L;

			@Test
			@DisplayName("권한이 없다는 메세지를 리턴한다")
			void itReturnsFailedToEvaluateExpressionMessage() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);
				given(studyService.getStudy(NOT_EXISTED_STUDY_ID)).willReturn(secondStudy);

				mockMvc.perform(get("/studies/update/{id}", NOT_EXISTED_STUDY_ID))
						.andDo(print())
						.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
						.andExpect(status().isForbidden());
			}
		}
	}

	@Nested
	@DisplayName("studyOpenList 메서드는")
	class Describe_studyOpenList {
		@Nested
		@DisplayName("로그인한 사용자가 주어진다면")
		class Context_WithAccount {
			@Test
			@DisplayName("모집중 스터디 조회 화면을 리턴한다")
			void itReturnsStudiesListView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/studies/open")
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("studies/studies-list")
						);
			}

			@Nested
			@DisplayName("로그인한 사용자가 주어지지 않는다면")
			class Context_WithNotAccount {

				@Test
				@DisplayName("아무런 데이터도 리턴하지 않는다")
				@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
				void itReturnsFailedToEvaluateExpressionMessage() throws Exception {

					mockMvc.perform(get("/studies/open"))
							.andDo(print())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(status().is5xxServerError());
				}
			}
		}
	}

	@Nested
	@DisplayName("studyCloseList 메서드는")
	class Describe_studyCloseList {
		@Nested
		@DisplayName("로그인한 사용자가 주어진다면")
		class Context_WithAccount {
			@Test
			@DisplayName("모집중 스터디 조회 화면을 리턴한다")
			void itReturnsStudiesListView() throws Exception {
				SecurityContextHolder.getContext().setAuthentication(accountToken);

				mockMvc.perform(get("/studies/close")
								.param("userAccount", objectMapper.writeValueAsString(userAccount))
						)
						.andExpect(status().isOk())
						.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
						.andExpect(view().name("studies/studies-list")
						);
			}

			@Nested
			@DisplayName("로그인한 사용자가 주어지지 않는다면")
			class Context_WithNotAccount {

				@Test
				@DisplayName("아무런 데이터도 리턴하지 않는다")
				@WithMockUser(username = "test", password = "password", roles = "ANONYMOUS")
				void itReturnsFailedToEvaluateExpressionMessage() throws Exception {

					mockMvc.perform(get("/studies/close"))
							.andDo(print())
							.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
							.andExpect(status().is5xxServerError());
				}
			}
		}
	}
}
