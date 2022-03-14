package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.AccountUpdatePasswordDto;
import com.example.bookclub.dto.AccountWithUploadFileCreateDto;
import com.example.bookclub.dto.AccountWithUploadFileUpdateDto;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountApiController.class)
class AccountApiControllerTest {
    private static final Long ACCOUNT_ID = 1L;
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_EMAIL = "accountEmail";
    private static final String ACCOUNT_NICKNAME = "accountNickname";
    private static final String ACCOUNT_PASSWORD = "accountPassword";

	private static final Long ACCOUNT_FILE_EXISTED_ID = 5L;
	private static final String ACCOUNT_FILE_NAME = "accountFileName";
	private static final String ACCOUNT_FILE_EMAIL = "accountFileEmail";
	private static final String ACCOUNT_FILE_NICKNAME = "accountFileNickname";
	private static final String ACCOUNT_FILE_PASSWORD = "accountFilePassword";

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

	private static final Long ACCOUNT_DELETED_ID = 4L;

	private static final Long ACCOUNT_NOT_EXISTED_ID = 999L;

	private UploadFile createdUploadFile;
	private UploadFile updatedUploadFile;

	private UploadFileCreateDto uploadFileCreateDto;
	private UploadFileResultDto uploadFileResultDto;

    private Account accountWithoutUploadFile;
	private Account accountWithUploadFile;
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

	private AccountResultDto accountCreatedWithUploadFileResultDto;
	private AccountResultDto accountCreatedWithoutUploadFileResultDto;
	private AccountResultDto accountUpdatedWithUploadFileResultDto;
	private AccountResultDto accountUpdatedWithoutUploadAlreadyHasUploadFileResultDto;
	private AccountResultDto accountUpdatedWithUploadBeforeNotHasUploadFileResultDto;
	private AccountResultDto accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto;
	private AccountResultDto accountUpdatedWithNewPasswordResultDto;
	private AccountResultDto deletedAccountResultDto;

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
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
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

		accountCreatedWithoutUploadFileResultDto = AccountResultDto.of(accountWithoutUploadFile);

		accountCreatedWithUploadFileResultDto = AccountResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_FILE_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(createdUploadFile))
				.build();

		accountUpdatedWithUploadFileResultDto = AccountResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(updatedUploadFile))
				.build();

		accountUpdatedWithoutUploadAlreadyHasUploadFileResultDto = AccountResultDto.builder()
				.id(ACCOUNT_FILE_EXISTED_ID)
				.name(ACCOUNT_FILE_NAME)
				.email(ACCOUNT_FILE_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_FILE_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(createdUploadFile))
				.build();

		accountUpdatedWithUploadBeforeNotHasUploadFileResultDto = AccountResultDto.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(updatedUploadFile))
				.build();

		accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto = AccountResultDto.builder()
				.id(ACCOUNT_ID)
				.name(ACCOUNT_NAME)
				.email(ACCOUNT_EMAIL)
				.nickname(ACCOUNT_UPDATED_NICKNAME)
				.password(ACCOUNT_PASSWORD)
				.uploadFileResultDto(UploadFileResultDto.of(null))
				.build();

		accountUpdatedWithNewPasswordResultDto = AccountResultDto.builder()
				.id(ACCOUNT_ID)
				.password(ACCOUNT_UPDATED_PASSWORD)
				.build();

		deletedAccountResultDto = AccountResultDto.builder()
				.id(ACCOUNT_DELETED_ID)
				.deleted(true)
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
        given(accountService.getAccount(ACCOUNT_ID)).willReturn(accountCreatedWithoutUploadFileResultDto);

        mockMvc.perform(
                        get("/api/users/{id}", ACCOUNT_ID)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(accountWithoutUploadFile.getId()));
    }

	@Test
	void detailWithNotExisted() throws Exception {
		given(accountService.getAccount(ACCOUNT_NOT_EXISTED_ID)).willThrow(AccountNotFoundException.class);

		mockMvc.perform(
				get("/api/user/{id}", ACCOUNT_NOT_EXISTED_ID)
		)
				.andDo(print())
//				.andExpect(content().string(containsString("User not found")))
				.andExpect(status().isNotFound());
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

					return AccountResultDto.builder()
							.id(ACCOUNT_FILE_EXISTED_ID)
							.name(accountWithUploadFile.getName())
							.email(accountWithUploadFile.getEmail())
							.nickname(accountWithUploadFile.getNickname())
							.password(accountWithUploadFile.getPassword())
							.uploadFileResultDto(UploadFileResultDto.of(uploadFile))
							.build();
				});

		mockMvc.perform(
				multipart("/api/users")
						.file(mockCreatedMultipartFile)
						.contentType("multipart/form-data")
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
				.andDo(print())
				.andExpect(jsonPath("id").value(ACCOUNT_FILE_EXISTED_ID))
				.andExpect(jsonPath("email").value(accountCreatedWithUploadFileResultDto.getEmail()))
				.andExpect(jsonPath("name").value(accountCreatedWithUploadFileResultDto.getName()))
				.andExpect(jsonPath("$.uploadFileResultDto.fileName",
						is(uploadFileResultDto.getFileName())))
				.andExpect(jsonPath("$.uploadFileResultDto.fileOriginalName",
						is(uploadFileResultDto.getFileOriginalName())))
				.andExpect(status().isCreated());
	}

    @Test
    void createWithoutUploadFile() throws Exception {
        given(accountService.createAccount(any(AccountCreateDto.class), eq(null)))
				.willReturn(accountCreatedWithoutUploadFileResultDto);

		mockMvc.perform(
				multipart("/api/users")
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
		.andExpect(status().isCreated());
	}

    @Test
    void createWithDuplicatedEmail() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(AccountEmailDuplicatedException.class);

		mockMvc.perform(
				multipart("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_DUPLICATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest());
    }

	@Test
	void createWithInvalidAuthenticationNumber() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(EmailNotAuthenticatedException.class);

		mockMvc.perform(
				multipart("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_INVALID_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest());
	}

    @Test
    void createWithDuplicatedNickname() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(AccountNicknameDuplicatedException.class);

		mockMvc.perform(
				multipart("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_DUPLICATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest());
    }

	@Test
	void createWithNotAuthenticatedEmailAuthenticationNumber() throws Exception {
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(createdUploadFile);
		given(accountService.createAccount(any(AccountCreateDto.class), any(UploadFile.class)))
				.willThrow(EmailNotAuthenticatedException.class);

		mockMvc.perform(
				multipart("/api/users")
						.file(mockCreatedMultipartFile)
						.param("name", ACCOUNT_CREATED_NAME)
						.param("email", ACCOUNT_CREATED_EMAIL)
						.param("nickname", ACCOUNT_CREATED_NICKNAME)
						.param("password", ACCOUNT_CREATED_PASSWORD)
						.param("authenticationNumber", ACCOUNT_CREATED_AUTHENTICATION_NUMBER)
		)
		.andDo(print())
		.andExpect(status().isBadRequest());
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
				multipart("/api/users/{id}", ACCOUNT_FILE_EXISTED_ID)
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
				);
    }

	// 사진 o -> 사진 업로드x
	@Test
	void updateWithoutUploadFileAlreadyHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithUploadFileToken);
		given(accountService.updateAccount(eq(ACCOUNT_FILE_EXISTED_ID), any(AccountUpdateDto.class), eq(null)))
				.willReturn(accountUpdatedWithoutUploadAlreadyHasUploadFileResultDto);

		mockMvc.perform(
						multipart("/api/users/{id}", ACCOUNT_FILE_EXISTED_ID)
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
				);
	}

	//사진 x -> 새로운 사진 업로드
	@Test
	void updateWithUploadFileBeforeNotHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(uploadFileService.upload(any(MultipartFile.class))).willReturn(updatedUploadFile);
		given(accountService.updateAccount(eq(ACCOUNT_ID), any(AccountUpdateDto.class), any(UploadFile.class)))
				.willReturn(accountUpdatedWithUploadBeforeNotHasUploadFileResultDto);

		mockMvc.perform(
						multipart("/api/users/{id}", ACCOUNT_ID)
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
				);
	}

	// 사진 x -> 사진 업로드x
	@Test
	void updateWithoutUploadFileBeforeNotHasUploadFile() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updateAccount(eq(ACCOUNT_ID), any(AccountUpdateDto.class), eq(null)))
				.willReturn(accountUpdatedWithoutUploadBeforeNotHasUploadFileResultDto);

		mockMvc.perform(
						multipart("/api/users/{id}", ACCOUNT_ID)
								.file(mockUpdatedMultipartFile)
								.param("nickname", ACCOUNT_UPDATED_NICKNAME)
								.param("password", ACCOUNT_PASSWORD)
				)
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("id").value(ACCOUNT_ID))
				.andExpect(jsonPath("email").value(ACCOUNT_EMAIL))
				.andExpect(jsonPath("nickname").value(accountUpdateDto.getNickname()))
				.andExpect(jsonPath("$.uploadFileResultDto.fileName", is("")))
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileOriginalName", is(""))
				)
				.andExpect(jsonPath(
						"$.uploadFileResultDto.fileUrl", is(""))
				);
	}

	@Test
	void updateWithNewPassword() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updatePassword(eq(ACCOUNT_ID), any(AccountUpdatePasswordDto.class)))
				.willReturn(accountUpdatedWithNewPasswordResultDto);

		mockMvc.perform(
				patch("/api/users/{id}/password", ACCOUNT_ID)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
		)
				.andDo(print())
				.andExpect(jsonPath("id").value(ACCOUNT_ID))
				.andExpect(jsonPath("password").value(accountUpdatePasswordDto.getNewPassword()));
	}

	@Test
	void updateWithInValidPassword() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updatePassword(eq(ACCOUNT_ID), any(AccountUpdatePasswordDto.class)))
				.willThrow(AccountPasswordBadRequestException.class);

		mockMvc.perform(
						patch("/api/users/{id}/password", ACCOUNT_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
				)
				.andDo(print())
				.andExpect(status().isBadRequest());
	}

	@Test
	void updateWithNotMatchedNewPassword() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.updatePassword(eq(ACCOUNT_ID), any(AccountUpdatePasswordDto.class)))
				.willThrow(new AccountNewPasswordNotMatchedException());

		mockMvc.perform(
						patch("/api/users/{id}/password", ACCOUNT_ID)
								.contentType(MediaType.APPLICATION_JSON)
								.content(objectMapper.writeValueAsString(accountUpdatePasswordDto))
				)
				.andDo(print())
				.andExpect(content().string(containsString("NewPassword not matched")))
				.andExpect(status().isBadRequest());
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
						delete("/api/users/{id}", ACCOUNT_DELETED_ID)
				)
				.andDo(print())
				.andExpect(jsonPath("id").value(ACCOUNT_DELETED_ID))
				.andExpect(jsonPath("deleted").value(true))
				.andExpect(status().isNoContent());
	}

	@Test
	void deleteNotAuthorizedAccount() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(deletedAccountToken);

		assertThatThrownBy(
				() -> mockMvc.perform(
								delete("/api/users/{id}", ACCOUNT_ID)
						)
						.andDo(print())
						.andExpect(status().isNoContent())
		)
				.hasCause(new AccessDeniedException("Access is denied"));
	}
}
