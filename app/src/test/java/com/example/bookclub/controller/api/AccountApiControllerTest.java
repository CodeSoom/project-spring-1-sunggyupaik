package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountCreateResultDto;
import com.example.bookclub.dto.AccountDeleteResultDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.dto.AccountUpdatePasswordResultDto;
import com.example.bookclub.dto.AccountUpdateResultDto;
import com.example.bookclub.dto.AccountWithUploadFileCreateDto;
import com.example.bookclub.dto.AccountWithUploadFileUpdateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.UploadFileCreateDto;
import com.example.bookclub.dto.UploadFileResultDto;
import com.example.bookclub.errors.AccountEmailDuplicatedException;
import com.example.bookclub.errors.AccountNewPasswordNotMatchedException;
import com.example.bookclub.errors.AccountNicknameDuplicatedException;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.AccountPasswordBadRequestException;
import com.example.bookclub.errors.EmailNotAuthenticatedException;
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
import org.springframework.mock.web.MockMultipartFile;
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
import org.springframework.web.multipart.MultipartFile;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

import static com.example.bookclub.utils.ApiDocumentUtils.getDocumentRequest;
import static com.example.bookclub.utils.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountApiController.class)
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "docs.api.com")
@ExtendWith({RestDocumentationExtension.class})
class AccountApiControllerTest {
    private static final Long ACCOUNT_ID = 1L;
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_EMAIL = "accountEmail";
    private static final String ACCOUNT_NICKNAME = "accountNickname";
    private static final String ACCOUNT_PASSWORD = "accountPassword";
	private static boolean ACCOUNT_DELETED = false;

	private static final Long ACCOUNT_FILE_EXISTED_ID = 5L;
	private static final String ACCOUNT_FILE_NAME = "accountFileName";
	private static final String ACCOUNT_FILE_EMAIL = "accountFileEmail";
	private static final String ACCOUNT_FILE_NICKNAME = "accountFileNickname";
	private static final String ACCOUNT_FILE_PASSWORD = "accountFilePassword";
	private static final boolean ACCOUNT_FILE_DELETED = false;

    private static final Long ACCOUNT_CREATED_ACCOUNT_ID = 2L;
    private static final String ACCOUNT_CREATED_NAME = "accountCreatName";
    private static final String ACCOUNT_CREATED_EMAIL = "accountCreateEmail";
    private static final String ACCOUNT_CREATED_NICKNAME = "accountCreateNickname";
	private static final String ACCOUNT_CREATED_PASSWORD = "accountCreatedPassword";
	private static final String ACCOUNT_CREATED_AUTHENTICATION_NUMBER = "accountCreatedAuthenticationNumber";

    private static final String ACCOUNT_UPDATED_NICKNAME = "accountUpdatedNickname";
    private static final String ACCOUNT_UPDATED_PASSWORD = "updatedPassword";

    private static final String ACCOUNT_DUPLICATED_EMAIL = "accountDuplicatedEmail";
	private static final String ACCOUNT_INVALID_AUTHENTICATION_NUMBER = "accountInvalidAuthenticationNumber";
	private static final String ACCOUNT_DUPLICATED_NICKNAME = "accountDuplicatedNickname";
	private static final String ACCOUNT_INVALID_EMAIL = "accountInvalidEmail";
	private static final String ACCOUNT_INVALID_PASSWORD = "invalidPassword";

    private static final Long FILE_CREATED_ID = 3L;
    private static final String FILE_CREATED_NAME = "createdFileName.jpg";
    private static final String FILE_CREATED_ORIGINAL_NAME = "createdOriginalName.jpg";
    private static final String FILE_CREATED_URL = "createdFileUrl";
	private static final String IMAGE_CONTENT_TYPE = "image/jpeg";

	private static final Long FILE_UPDATED_ID = 6L;
	private static final String FILE_UPDATED_NAME = "updatedFileName.jpg";
	private static final String FILE_UPDATED_ORIGINAL_NAME = "updatedOriginalName.jpg";
	private static final String FILE_UPDATED_URL = "updatedFileUrl";

	private static final Long STUDY_ID = 5L;
	private static final String STUDY_NAME = "studyName";
	private static final String STUDY_BOOK_NAME = "studyBookName";
	private static final String STUDY_BOOK_IMAGE = "studyImage";
	private static final String STUDY_EMAIL = "studyEmail";
	private static final String STUDY_DESCRIPTION = "studyDescription";
	private static final String STUDY_CONTACT = "studyContact";
	private static final int STUDY_SIZE = 10;
	private static final int STUDY_APPLY_COUNT = 10;
	private static final LocalDate STUDY_START_DATE = LocalDate.now().plusDays(1);
	private static final LocalDate STUDY_END_DATE = LocalDate.now().plusDays(2);
	private static final String STUDY_START_TIME = "12";
	private static final String STUDY_END_TIME = "14";
	private static final Day STUDY_DAY = Day.MONDAY;
	private static final StudyState STUDY_STUDY_STATE = StudyState.OPEN;
	private static final Zone STUDY_ZONE = Zone.SEOUL;
	private static final int STUDY_LIKES_COUNT  = 10;
	private static final boolean STUDY_LIKED = false;
	private static final int STUDY_COMMENTS_COUNT = 3;
	private static final boolean STUDY_IS_FAVORITE = false;

	private static final Long ACCOUNT_DELETED_ID = 4L;

	private static final Long ACCOUNT_NOT_EXISTED_ID = 999L;

	private UploadFile createdUploadFile;
	private UploadFile updatedUploadFile;

	private Study study;

	private UploadFileCreateDto uploadFileCreateDto;
	private UploadFileResultDto uploadFileResultDto;

    private Account accountWithoutUploadFile;
	private Account accountWithUploadFile;
	private AccountResultDto createdAccountWithUploadFile;
    private Account createdAccount;
	private Account updatedAccount;
	private Account deletedAccount;

	private UsernamePasswordAuthenticationToken accountWithoutUploadFileToken;
	private UsernamePasswordAuthenticationToken accountWithUploadFileToken;
	private UsernamePasswordAuthenticationToken deletedAccountToken;

    private AccountCreateDto accountCreateDto;
    private AccountUpdateDto accountUpdateDto;
	private AccountUpdatePasswordDto accountUpdatePasswordDto;
	private AccountUpdatePasswordDto accountUpdateInvalidPasswordDto;
	private AccountUpdatePasswordDto accountUpdateNotMatchedNewPasswordDto;

	private AccountResultDto accountWithUploadFileResultDto;
	private AccountCreateResultDto accountCreatedWithoutUploadFileResultDto;
	private AccountUpdateResultDto accountUpdatedWithUploadFileResultDto;
	private AccountUpdateResultDto accountUpdatedWithoutUploadAlreadyHasUploadFileResultDto;
	private AccountUpdateResultDto accountUpdatedWithUploadBeforeNotHasUploadFileResultDto;
	private AccountUpdateResultDto accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto;
	private AccountUpdatePasswordResultDto accountUpdatedWithNewPasswordResultDto;
	private AccountDeleteResultDto deletedAccountResultDto;

	private MockMultipartFile mockCreatedMultipartFile;
	private MockMultipartFile mockUpdatedMultipartFile;
	private AccountWithUploadFileCreateDto accountWithUploadFileCreateDto;
	private AccountWithUploadFileUpdateDto accountWithUploadFileUpdateDto;

	@Autowired
	MockMvc mockMvc;

	@Autowired
	private WebApplicationContext ctx;

	@MockBean
	AccountService accountService;

	@MockBean
	UploadFileService uploadFileService;

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

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
				.apply(documentationConfiguration(restDocumentationContextProvider))
                .alwaysDo(print())
                .build();

		createdUploadFile = UploadFile.builder()
				.id(FILE_CREATED_ID)
				.fileName(FILE_CREATED_NAME)
				.fileOriginalName(FILE_CREATED_ORIGINAL_NAME)
				.fileUrl(FILE_CREATED_URL)
				.build();

		updatedUploadFile = UploadFile.builder()
				.id(FILE_UPDATED_ID)
				.fileName(FILE_UPDATED_NAME)
				.fileOriginalName(FILE_UPDATED_ORIGINAL_NAME)
				.fileUrl(FILE_UPDATED_URL)
				.build();

		study = Study.builder()
				.id(STUDY_ID)
				.name(STUDY_NAME)
				.bookName(STUDY_BOOK_NAME)
				.bookImage(STUDY_BOOK_IMAGE)
				.email(STUDY_EMAIL)
				.description(STUDY_DESCRIPTION)
				.contact(STUDY_CONTACT)
				.size(STUDY_SIZE)
				.applyCount(STUDY_APPLY_COUNT)
				.startDate(STUDY_START_DATE)
				.endDate(STUDY_END_DATE)
				.startTime(STUDY_START_TIME)
				.endTime(STUDY_END_TIME)
				.day(STUDY_DAY)
				.studyState(STUDY_STUDY_STATE)
				.zone(STUDY_ZONE)
				.likesCount(STUDY_LIKES_COUNT)
				.liked(STUDY_LIKED)
				.commentsCount(STUDY_COMMENTS_COUNT)
				.isFavorite(STUDY_IS_FAVORITE)
				.build();

		uploadFileCreateDto = UploadFileCreateDto.builder()
				.fileName(FILE_CREATED_NAME)
				.fileOriginalName(FILE_CREATED_ORIGINAL_NAME)
				.fileUrl(FILE_CREATED_URL)
				.build();

		uploadFileResultDto = UploadFileResultDto.builder()
				.fileName(FILE_CREATED_NAME)
				.fileOriginalName(FILE_CREATED_ORIGINAL_NAME)
				.fileUrl(FILE_CREATED_URL)
				.build();

        accountWithoutUploadFile = Account.builder()
                .id(ACCOUNT_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(ACCOUNT_PASSWORD)
                .build();

		accountWithUploadFile = Account.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_FILE_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.build();

		accountWithUploadFile.addUploadFile(createdUploadFile);

        createdAccount = Account.builder()
				.id(ACCOUNT_CREATED_ACCOUNT_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
                .build();

		createdAccountWithUploadFile = AccountResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_FILE_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(createdUploadFile))
				.build();

		updatedAccount = Account.builder()
				.name(ACCOUNT_CREATED_NAME)
				.email(ACCOUNT_CREATED_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_CREATED_PASSWORD)
				.build();

		deletedAccount = Account.builder()
				.id(ACCOUNT_DELETED_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.deleted(false)
				.build();

		accountWithoutUploadFileToken = new UsernamePasswordAuthenticationToken(
			new UserAccount(accountWithoutUploadFile, List.of(new SimpleGrantedAuthority("USER"))),
			accountWithoutUploadFile.getPassword(),
			List.of(new SimpleGrantedAuthority("USER")));

		accountWithUploadFileToken = new UsernamePasswordAuthenticationToken(
				new UserAccount(accountWithUploadFile, List.of(new SimpleGrantedAuthority("USER"))),
				accountWithUploadFile.getPassword(),
				List.of(new SimpleGrantedAuthority("USER")));

		deletedAccountToken = new UsernamePasswordAuthenticationToken(
				new UserAccount(deletedAccount, List.of(new SimpleGrantedAuthority("USER"))),
				deletedAccount.getPassword(),
				List.of(new SimpleGrantedAuthority("USER")));

        accountCreateDto = AccountCreateDto.builder()
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(ACCOUNT_CREATED_PASSWORD)
				.authenticationNumber(ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(ACCOUNT_UPDATED_NICKNAME)
                .build();

		accountUpdatePasswordDto = AccountUpdatePasswordDto.builder()
				.password(ACCOUNT_PASSWORD)
				.newPassword(ACCOUNT_UPDATED_PASSWORD)
				.newPasswordConfirmed(ACCOUNT_UPDATED_PASSWORD)
				.build();

		accountUpdateInvalidPasswordDto = AccountUpdatePasswordDto.builder()
				.password(ACCOUNT_INVALID_PASSWORD)
				.newPassword(ACCOUNT_UPDATED_PASSWORD)
				.newPasswordConfirmed(ACCOUNT_UPDATED_PASSWORD)
				.build();

		accountUpdateNotMatchedNewPasswordDto = AccountUpdatePasswordDto.builder()
				.password(ACCOUNT_PASSWORD)
				.newPassword(ACCOUNT_UPDATED_PASSWORD)
				.newPasswordConfirmed(ACCOUNT_PASSWORD)
				.build();

		accountCreatedWithoutUploadFileResultDto = AccountCreateResultDto.of(accountWithoutUploadFile);
		accountCreatedWithoutUploadFileResultDto.setUploadFileResultDto(null);

		accountWithUploadFileResultDto = AccountResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_FILE_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.deleted(ACCOUNT_FILE_DELETED)
				.uploadFileResultDto(UploadFileResultDto.of(createdUploadFile))
				.studyResultDto(StudyResultDto.of(study))
				.build();

		accountUpdatedWithUploadFileResultDto = AccountUpdateResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(updatedUploadFile))
				.build();

		accountUpdatedWithoutUploadAlreadyHasUploadFileResultDto = AccountUpdateResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(createdUploadFile))
				.build();

		accountUpdatedWithUploadBeforeNotHasUploadFileResultDto = AccountUpdateResultDto.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(updatedUploadFile))
				.build();

		accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto = AccountUpdateResultDto.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.build();

		accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto.setUploadFileResultDto(null);

		accountUpdatedWithNewPasswordResultDto = AccountUpdatePasswordResultDto.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_NICKNAME)
				.password(ACCOUNT_UPDATED_PASSWORD)
				.deleted(ACCOUNT_DELETED)
				.build();

		deletedAccountResultDto = AccountDeleteResultDto.builder()
				.id(ACCOUNT_DELETED_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_NICKNAME)
				.password(ACCOUNT_UPDATED_PASSWORD)
				.deleted(true)
				.uploadFileResultDto(UploadFileResultDto.of(createdUploadFile))
				.build();

		mockCreatedMultipartFile = new MockMultipartFile(
				"uploadFile", FILE_CREATED_ORIGINAL_NAME, IMAGE_CONTENT_TYPE, "test data".getBytes()
		);

		mockUpdatedMultipartFile = new MockMultipartFile(
				"uploadFile", FILE_UPDATED_ORIGINAL_NAME, IMAGE_CONTENT_TYPE, "test data".getBytes()
		);

		accountWithUploadFileCreateDto = AccountWithUploadFileCreateDto.builder()
				.accountCreateDto(accountCreateDto)
				.multipartFile(mockCreatedMultipartFile)
				.build();

		accountWithUploadFileUpdateDto = AccountWithUploadFileUpdateDto.builder()
				.accountUpdateDto(accountUpdateDto)
				.multipartFile(mockCreatedMultipartFile)
				.build();
    }

    @Test
    void detailWithExistedId() throws Exception {
        given(accountService.getAccount(ACCOUNT_ID)).willReturn(accountWithUploadFileResultDto);

		this.mockMvc.perform(
						RestDocumentationRequestBuilders.get("/api/users/{id}", ACCOUNT_ID)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(accountWithUploadFileResultDto.getId()))
				.andDo(document("user-detail",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto.id").type(NUMBER).description("파일 식별자"),
								fieldWithPath("uploadFileResultDto.fileName").type(STRING).description("파일명"),
								fieldWithPath("uploadFileResultDto.fileOriginalName").type(STRING).description("파일 원본명"),
								fieldWithPath("uploadFileResultDto.fileUrl").type(STRING).description("파일 URL"),
								fieldWithPath("studyResultDto.id").type(NUMBER).description("스터디 식별자"),
								fieldWithPath("studyResultDto.name").type(STRING).description("이름"),
								fieldWithPath("studyResultDto.bookName").type(STRING).description("책이름"),
								fieldWithPath("studyResultDto.bookImage").type(STRING).description(" 책 이미지"),
								fieldWithPath("studyResultDto.email").type(STRING).description("이메일"),
								fieldWithPath("studyResultDto.description").type(STRING).description("설명"),
								fieldWithPath("studyResultDto.contact").type(STRING).description("연락처"),
								fieldWithPath("studyResultDto.size").type(NUMBER).description("정원 수"),
								fieldWithPath("studyResultDto.applyCount").type(NUMBER).description("지원 수"),
								fieldWithPath("studyResultDto.startDate").type(STRING).description("시작 날짜"),
								fieldWithPath("studyResultDto.endDate").type(STRING).description("종료 날짜"),
								fieldWithPath("studyResultDto.startTime").type(STRING).description("시작 시간"),
								fieldWithPath("studyResultDto.endTime").type(STRING).description("종료 시간"),
								fieldWithPath("studyResultDto.day").type(STRING).description("날짜"),
								fieldWithPath("studyResultDto.studyState").type(STRING).description("스터디 상태"),
								fieldWithPath("studyResultDto.zone").type(STRING).description("지역"),
								fieldWithPath("studyResultDto.likesCount").type(NUMBER).description("좋아요 수"),
								fieldWithPath("studyResultDto.liked").type(BOOLEAN).description("좋아요 여부"),
								fieldWithPath("studyResultDto.commentsCount").type(NUMBER).description("댓글 수"),
								fieldWithPath("studyResultDto.favorite").type(BOOLEAN).description("즐겨찾기 여부")
						)
				));
	}

	@Test
	void detailWithNotExisted() throws Exception {
		given(accountService.getAccount(ACCOUNT_NOT_EXISTED_ID))
				.willThrow(new AccountNotFoundException(ACCOUNT_NOT_EXISTED_ID));

		mockMvc.perform(
				RestDocumentationRequestBuilders.get("/api/users/{id}", ACCOUNT_NOT_EXISTED_ID)
		)
				.andDo(print())
//				.andExpect(content().string(containsString("User not found")))
				.andExpect(status().isNotFound())
				.andDo(document("user-detail-not-existed-id",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						responseFields(
								fieldWithPath("message").type(STRING).description("예외 메세지")
						)
				));
	}

	@Test
	void createWithAllValidAttributes() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.will(invocation -> {
					AccountCreateDto accountCreateDto = invocation.getArgument(0);
					UploadFile uploadFile = invocation.getArgument(1);
					uploadFile = UploadFile.builder()
							.id(FILE_CREATED_ID)
							.fileName(FILE_CREATED_NAME)
							.fileOriginalName(FILE_CREATED_ORIGINAL_NAME)
							.fileUrl(FILE_CREATED_URL)
							.build();

					return AccountCreateResultDto.builder()
							.id(ACCOUNT_CREATED_ACCOUNT_ID)
							.name(accountWithUploadFile.getName())
							.email(accountWithUploadFile.getEmail())
							.nickname(accountWithUploadFile.getNickname())
							.password(accountWithUploadFile.getPassword())
							.uploadFileResultDto(UploadFileResultDto.of(uploadFile))
							.build();
				});

		mockMvc.perform(
						RestDocumentationRequestBuilders.fileUpload("/api/users")
						.file(mockCreatedMultipartFile)
						.contentType("multipart/form-data")
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
				.andDo(print())
				.andExpect(jsonPath("id").value(ACCOUNT_CREATED_ACCOUNT_ID))
				.andExpect(jsonPath("email").value(accountWithUploadFileResultDto.getEmail()))
				.andExpect(jsonPath("name").value(accountWithUploadFileResultDto.getName()))
				.andExpect(jsonPath("$.uploadFileResultDto.fileName",
						is(uploadFileResultDto.getFileName())))
				.andExpect(jsonPath("$.uploadFileResultDto.fileOriginalName",
						is(uploadFileResultDto.getFileOriginalName())))
				.andExpect(status().isCreated())
				.andDo(document("user-create",
						getDocumentRequest(),
						getDocumentResponse(),
						requestParts(partWithName("uploadFile").description("업로드 파일")),
						requestParameters(
								parameterWithName("name").description("이름"),
								parameterWithName("email").description("이메일"),
								parameterWithName("nickname").description("닉네임"),
								parameterWithName("password").description("비밀번호"),
								parameterWithName("authenticationNumber").description("인증번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto.id").type(NUMBER).description("파일 식별자"),
								fieldWithPath("uploadFileResultDto.fileName").type(STRING).description("파일명"),
								fieldWithPath("uploadFileResultDto.fileOriginalName").type(STRING).description("파일 원본명"),
								fieldWithPath("uploadFileResultDto.fileUrl").type(STRING).description("파일 URL")
						)
				));
	}

    @Test
    void createWithoutUploadFile() throws Exception {
        given(accountService.createAccount(any(AccountCreateDto.class), eq(null)))
				.willReturn(accountCreatedWithoutUploadFileResultDto);

		mockMvc.perform(
				RestDocumentationRequestBuilders.fileUpload("/api/users")
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(jsonPath("id").value(ACCOUNT_ID))
		.andExpect(jsonPath("email").value(accountCreatedWithoutUploadFileResultDto.getEmail()))
		.andExpect(jsonPath("name").value(accountCreatedWithoutUploadFileResultDto.getName()))
		.andExpect(status().isCreated())
		.andDo(document("user-create-without-upload-file",
				getDocumentRequest(),
				getDocumentResponse(),
				requestParameters(
						parameterWithName("name").description("이름"),
						parameterWithName("email").description("이메일"),
						parameterWithName("nickname").description("닉네임"),
						parameterWithName("password").description("비밀번호"),
						parameterWithName("authenticationNumber").description("인증번호")
				),
				responseFields(
						fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
						fieldWithPath("name").type(STRING).description("이름"),
						fieldWithPath("email").type(STRING).description("이메일"),
						fieldWithPath("nickname").type(STRING).description("닉네임"),
						fieldWithPath("password").type(STRING).description("비밀번호"),
						fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
						fieldWithPath("uploadFileResultDto").description("업로드 사진")
				)
		));
	}

    @Test
    void createWithDuplicatedEmail() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(new AccountEmailDuplicatedException(ACCOUNT_DUPLICATED_EMAIL));

		mockMvc.perform(
				RestDocumentationRequestBuilders.fileUpload("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_DUPLICATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andDo(document("user-create-duplicated-email",
				getDocumentRequest(),
				getDocumentResponse(),
				requestParameters(
						parameterWithName("name").description("이름"),
						parameterWithName("email").description("이메일"),
						parameterWithName("nickname").description("닉네임"),
						parameterWithName("password").description("비밀번호"),
						parameterWithName("authenticationNumber").description("인증번호")
				),
				responseFields(
						fieldWithPath("message").type(STRING).description("예외 메세지")
				)
		));
    }

	@Test
	void createWithInvalidAuthenticationNumber() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(new EmailNotAuthenticatedException(ACCOUNT_INVALID_AUTHENTICATION_NUMBER));

		mockMvc.perform(
				RestDocumentationRequestBuilders.fileUpload("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_INVALID_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andDo(document("user-create-invalid-authentication-number",
				getDocumentRequest(),
				getDocumentResponse(),
				requestParameters(
						parameterWithName("name").description("이름"),
						parameterWithName("email").description("이메일"),
						parameterWithName("nickname").description("닉네임"),
						parameterWithName("password").description("비밀번호"),
						parameterWithName("authenticationNumber").description("인증번호")
				),
				responseFields(
						fieldWithPath("message").type(STRING).description("예외 메세지")
				)
		));
	}

    @Test
    void createWithDuplicatedNickname() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(new AccountNicknameDuplicatedException(ACCOUNT_DUPLICATED_NICKNAME));

		mockMvc.perform(
				RestDocumentationRequestBuilders.fileUpload("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_DUPLICATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest())
		.andDo(document("user-create-duplicated-nickname",
				getDocumentRequest(),
				getDocumentResponse(),
				requestParameters(
						parameterWithName("name").description("이름"),
						parameterWithName("email").description("이메일"),
						parameterWithName("nickname").description("닉네임"),
						parameterWithName("password").description("비밀번호"),
						parameterWithName("authenticationNumber").description("인증번호")
				),
				responseFields(
						fieldWithPath("message").type(STRING).description("예외 메세지")
				)
		));
    }

	@Test
	void updateNotAuthorizedAccount() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);

		assertThatThrownBy(
				() -> mockMvc.perform(
								multipart("/api/users/{id}", ACCOUNT_FILE_EXISTED_ID)
						)
						.andDo(print())
						.andExpect(status().isOk())
		)
				.hasCause(new AccessDeniedException("Access is denied"));
	}

	// 사진 o -> 새로운 사진 업로드
    @Test
    void updateWithAllValidAttributesAlreadyHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithUploadFileToken);
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(updatedUploadFile);
		given(accountService.updateAccount(eq(ACCOUNT_FILE_EXISTED_ID), any(AccountUpdateDto.class), any(UploadFile.class)))
				.willReturn(accountUpdatedWithUploadFileResultDto);

        mockMvc.perform(
						RestDocumentationRequestBuilders.fileUpload("/api/users/{id}", ACCOUNT_FILE_EXISTED_ID)
						.file(mockUpdatedMultipartFile)
						.param("nickname", ACCOUNT_UPDATED_NICKNAME)
						.param("password", ACCOUNT_FILE_PASSWORD)
        )
                .andDo(print())
                .andExpect(status().isOk())
				.andExpect(jsonPath("id").value(ACCOUNT_FILE_EXISTED_ID))
				.andExpect(jsonPath("email").value(ACCOUNT_FILE_EMAIL))
				.andExpect(jsonPath("nickname").value(accountUpdateDto.getNickname()))
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileName", is(updatedUploadFile.getFileName()))
				)
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileOriginalName", is(updatedUploadFile.getFileOriginalName()))
				)
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileUrl", is(updatedUploadFile.getFileUrl()))
				)
				.andDo(document("user-update",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestParts(
								partWithName("uploadFile").description("업로드 파일")
						),
						requestParameters(
								parameterWithName("nickname").description("닉네임"),
								parameterWithName("password").description("비밀번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto.id").type(NUMBER).description("파일 식별자"),
								fieldWithPath("uploadFileResultDto.fileName").type(STRING).description("파일명"),
								fieldWithPath("uploadFileResultDto.fileOriginalName").type(STRING).description("파일 원본명"),
								fieldWithPath("uploadFileResultDto.fileUrl").type(STRING).description("파일 URL")
						)
				));
    }

	// 사진 o -> 사진 업로드x
	@Test
	void updateWithoutUploadFileAlreadyHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithUploadFileToken);
		given(accountService.updateAccount(eq(ACCOUNT_FILE_EXISTED_ID), any(AccountUpdateDto.class), eq(null)))
				.willReturn(accountUpdatedWithoutUploadAlreadyHasUploadFileResultDto);

		mockMvc.perform(
						RestDocumentationRequestBuilders.fileUpload("/api/users/{id}", ACCOUNT_FILE_EXISTED_ID)
								.param("nickname", ACCOUNT_UPDATED_NICKNAME)
								.param("password", ACCOUNT_FILE_PASSWORD)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(ACCOUNT_FILE_EXISTED_ID))
				.andExpect(jsonPath("email").value(ACCOUNT_FILE_EMAIL))
				.andExpect(jsonPath("nickname").value(accountUpdateDto.getNickname()))
				.andExpect(jsonPath("$.uploadFileResultDto.fileName", is(createdUploadFile.getFileName())))
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileOriginalName", is(createdUploadFile.getFileOriginalName()))
				)
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileUrl", is(createdUploadFile.getFileUrl()))
				)
				.andDo(document("user-update-without-upload-file",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestParameters(
								parameterWithName("nickname").description("닉네임"),
								parameterWithName("password").description("비밀번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto.id").type(NUMBER).description("파일 식별자"),
								fieldWithPath("uploadFileResultDto.fileName").type(STRING).description("파일명"),
								fieldWithPath("uploadFileResultDto.fileOriginalName").type(STRING).description("파일 원본명"),
								fieldWithPath("uploadFileResultDto.fileUrl").type(STRING).description("파일 URL")
						)
				));
	}

	//사진 x -> 새로운 사진 업로드
	@Test
	void updateWithUploadFileBeforeNotHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(updatedUploadFile);
		given(accountService.updateAccount(eq(ACCOUNT_ID), any(AccountUpdateDto.class), any(UploadFile.class)))
				.willReturn(accountUpdatedWithUploadBeforeNotHasUploadFileResultDto);

		mockMvc.perform(
						RestDocumentationRequestBuilders.fileUpload("/api/users/{id}", ACCOUNT_ID)
								.file(mockUpdatedMultipartFile)
								.param("nickname", ACCOUNT_UPDATED_NICKNAME)
								.param("password", ACCOUNT_PASSWORD)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(ACCOUNT_ID))
				.andExpect(jsonPath("email").value(ACCOUNT_EMAIL))
				.andExpect(jsonPath("nickname").value(accountUpdateDto.getNickname()))
				.andExpect(jsonPath("$.uploadFileResultDto.fileName", is(updatedUploadFile.getFileName())))
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileOriginalName", is(updatedUploadFile.getFileOriginalName()))
				)
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileUrl", is(updatedUploadFile.getFileUrl()))
				)
				.andDo(document("user-update-with-first-upload-file",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestParts(
								partWithName("uploadFile").description("업로드 파일")
						),
						requestParameters(
								parameterWithName("nickname").description("닉네임"),
								parameterWithName("password").description("비밀번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto.id").type(NUMBER).description("파일 식별자"),
								fieldWithPath("uploadFileResultDto.fileName").type(STRING).description("파일명"),
								fieldWithPath("uploadFileResultDto.fileOriginalName").type(STRING).description("파일 원본명"),
								fieldWithPath("uploadFileResultDto.fileUrl").type(STRING).description("파일 URL")
						)
				));
	}

	// 사진 x -> 사진 업로드x
	@Test
	void updateWithoutUploadFileBeforeNotHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updateAccount(eq(ACCOUNT_ID), any(AccountUpdateDto.class), eq(null)))
				.willReturn(accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto);

		mockMvc.perform(
						RestDocumentationRequestBuilders.fileUpload("/api/users/{id}", ACCOUNT_ID)
								.file(mockUpdatedMultipartFile)
								.param("nickname", ACCOUNT_UPDATED_NICKNAME)
								.param("password", ACCOUNT_PASSWORD)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(ACCOUNT_ID))
				.andExpect(jsonPath("email").value(ACCOUNT_EMAIL))
				.andExpect(jsonPath("nickname").value(accountUpdateDto.getNickname()))
				.andDo(document("user-update-never-upload-file",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestParameters(
								parameterWithName("nickname").description("닉네임"),
								parameterWithName("password").description("비밀번호")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto").description("업로드 사진")
						)
				));
	}

	@Test
	void updateWithNewPassword() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updatePassword(eq(ACCOUNT_ID), any(AccountUpdatePasswordDto.class)))
				.willReturn(accountUpdatedWithNewPasswordResultDto);

		mockMvc.perform(
				RestDocumentationRequestBuilders.patch("/api/users/{id}/password", ACCOUNT_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
		)
				.andDo(print())
				.andExpect(jsonPath("id").value(ACCOUNT_ID))
				.andExpect(jsonPath("password").value(accountUpdatePasswordDto.getNewPassword()))
				.andDo(document("user-password-update",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestFields(
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("newPassword").type(STRING).description("새로운 비밀번호"),
								fieldWithPath("newPasswordConfirmed").type(STRING).description("새로운 비밀번호 확인")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부")
						)
				));
	}

	@Test
	void updateWithInValidPassword() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updatePassword(eq(ACCOUNT_ID), any(AccountUpdatePasswordDto.class)))
				.willThrow(new AccountPasswordBadRequestException());

		mockMvc.perform(
						RestDocumentationRequestBuilders.patch("/api/users/{id}/password", ACCOUNT_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andDo(document("user-password-update-invalid",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestFields(
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("newPassword").type(STRING).description("새로운 비밀번호"),
								fieldWithPath("newPasswordConfirmed").type(STRING).description("새로운 비밀번호 확인")
						),
						responseFields(
								fieldWithPath("message").type(STRING).description("예외 메세지")
						)
				));
	}

	@Test
	void updateWithNotMatchedNewPassword() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updatePassword(eq(ACCOUNT_ID), any(AccountUpdatePasswordDto.class)))
				.willThrow(new AccountNewPasswordNotMatchedException());

		mockMvc.perform(
						RestDocumentationRequestBuilders.patch("/api/users/{id}/password", ACCOUNT_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
				)
				.andDo(print())
				.andExpect(content().string(containsString("NewPassword not matched")))
				.andExpect(status().isBadRequest())
				.andDo(print())
				.andExpect(status().isBadRequest())
				.andDo(document("user-password-update-not-matched",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						requestFields(
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("newPassword").type(STRING).description("새로운 비밀번호"),
								fieldWithPath("newPasswordConfirmed").type(STRING).description("새로운 비밀번호 확인")
						),
						responseFields(
								fieldWithPath("message").type(STRING).description("예외 메세지")
						)
				));
	}

	@Test
	void updatePasswordNotAuthorizedAccount() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithUploadFileToken);

		assertThatThrownBy(
				() -> mockMvc.perform(
								patch("/api/users/{id}/password", ACCOUNT_ID)
										.contentType(MediaType.APPLICATION_JSON)
										.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
						)
						.andDo(print())
						.andExpect(status().isOk())
		)
				.hasCause(new AccessDeniedException("Access is denied"));
	}

	@Test
	void deleteWithExistedId() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(deletedAccountToken);
		given(accountService.deleteAccount(ACCOUNT_DELETED_ID)).willReturn(deletedAccountResultDto);

		mockMvc.perform(
						RestDocumentationRequestBuilders.delete("/api/users/{id}", ACCOUNT_DELETED_ID)
				)
				.andDo(print())
				.andExpect(jsonPath("id").value(ACCOUNT_DELETED_ID))
				.andExpect(jsonPath("deleted").value(true))
				.andExpect(status().isNoContent())
				.andDo(document("user-delete",
						getDocumentRequest(),
						getDocumentResponse(),
						pathParameters(
								parameterWithName("id").description("사용자 식별자")
						),
						responseFields(
								fieldWithPath("id").type(NUMBER).description("사용자 식별자"),
								fieldWithPath("name").type(STRING).description("이름"),
								fieldWithPath("email").type(STRING).description("이메일"),
								fieldWithPath("nickname").type(STRING).description("닉네임"),
								fieldWithPath("password").type(STRING).description("비밀번호"),
								fieldWithPath("deleted").type(BOOLEAN).description("삭제 여부"),
								fieldWithPath("uploadFileResultDto.id").type(NUMBER).description("파일 식별자"),
								fieldWithPath("uploadFileResultDto.fileName").type(STRING).description("파일명"),
								fieldWithPath("uploadFileResultDto.fileOriginalName").type(STRING).description("파일 원본명"),
								fieldWithPath("uploadFileResultDto.fileUrl").type(STRING).description("파일 URL")
						)
				));
	}

	@Test
	void deleteNotAuthorizedAccount() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(deletedAccountToken);
		given(accountService.deleteAccount(ACCOUNT_DELETED_ID))
				.willThrow(new AccessDeniedException("Access is denied"));

		assertThatThrownBy(
				() -> mockMvc.perform(
								RestDocumentationRequestBuilders.delete("/api/users/{id}", ACCOUNT_ID)
						)
						.andDo(print())
						.andExpect(status().isNoContent())
						.andDo(document("user-delete-not-existed-id",
								getDocumentRequest(),
								getDocumentResponse(),
								pathParameters(
										parameterWithName("id").description("사용자 식별자")
								),
								responseFields(
										fieldWithPath("message").type(NUMBER).description("예외 메세지")
								)
						))
		);
	}
}
