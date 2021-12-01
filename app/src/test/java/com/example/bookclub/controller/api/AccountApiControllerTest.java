package com.example.bookclub.controller.api;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.UploadFileService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountResultDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.example.bookclub.dto.UploadFileCreateDto;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.security.AccountAuthenticationService;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.UserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountApiController.class)
class AccountApiControllerTest {
    private static final Long ACCOUNT_EXISTED_ID = 1L;
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_EMAIL = "accountEmail";
    private static final String ACCOUNT_NICKNAME = "accountNickname";
    private static final String ACCOUNT_PASSWORD = "accountPassword";

    private static final Long CREATED_ACCOUNT_ID = 2L;
    private static final String CREATED_NAME = "creatName";
    private static final String CREATED_EMAIL = "createEmail";
    private static final String CREATED_NICKNAME = "createNickname";
    private static final String CREATED_PASSWORD = "0987654321";

    private static final String UPDATED_NICKNAME = "qwer";
    private static final String UPDATED_PASSWORD = "5678";

    private static final String EXISTED_EMAIL = "email";

    private static final Long CREATED_FILE_ID = 3L;
    private static final String CREATED_FILE_NAME = "createdFileName.jpg";
    private static final String CREATED_FILE_ORIGINAL_NAME = "createdOriginalName.jpg";
    private static final String CREATED_FILE_URL = "createdFileUrl";

	private static final Long DELETED_ACCOUNT_ID = 4L;

	private static final Long NOT_EXISTED_ID = 2L;

    private Account accountWithoutUploadFile;
    private Account createdAccount;
	private Account deletedAccount;

	private UsernamePasswordAuthenticationToken accountWithoutUploadFileToken;
	private UsernamePasswordAuthenticationToken deletedAccountToken;

    private AccountCreateDto accountCreateDto;
    private AccountUpdateDto accountUpdateDto;
	private AccountResultDto accountResultDto;
	private AccountResultDto deletedAccountResultDto;

    private UploadFileCreateDto uploadFileCreateDto;

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
	private PersistentTokenRepository tokenRepository;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        accountWithoutUploadFile = Account.builder()
                .id(ACCOUNT_EXISTED_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(ACCOUNT_PASSWORD)
                .build();

        createdAccount = Account.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

		deletedAccount = Account.builder()
				.id(DELETED_ACCOUNT_ID)
				.deleted(true)
				.build();

		accountWithoutUploadFileToken = new UsernamePasswordAuthenticationToken(
			new UserAccount(accountWithoutUploadFile, List.of(new SimpleGrantedAuthority("USER"))),
			accountWithoutUploadFile.getPassword(),
			List.of(new SimpleGrantedAuthority("USER")));

		accountWithoutUploadFileToken = new UsernamePasswordAuthenticationToken(
				new UserAccount(deletedAccount, List.of(new SimpleGrantedAuthority("USER"))),
				deletedAccount.getPassword(),
				List.of(new SimpleGrantedAuthority("USER")));

        accountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(UPDATED_NICKNAME)
                .build();

		accountResultDto = AccountResultDto.of(accountWithoutUploadFile);
		deletedAccountResultDto = AccountResultDto.of(deletedAccount);

        uploadFileCreateDto = UploadFileCreateDto.builder()
                .fileName(CREATED_FILE_NAME)
                .fileOriginalName(CREATED_FILE_ORIGINAL_NAME)
                .fileUrl(CREATED_FILE_URL)
                .build();
    }

    @Test
    void detailWithExistedId() throws Exception {
        given(accountService.getUser(ACCOUNT_EXISTED_ID)).willReturn(accountResultDto);

        mockMvc.perform(
                        get("/api/users/{id}", ACCOUNT_EXISTED_ID)
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(accountWithoutUploadFile.getId()));
    }

	@Test
	void detailWithNotExisted() throws Exception {
		given(accountService.getUser(NOT_EXISTED_ID)).willThrow(AccountNotFoundException.class);

		mockMvc.perform(
				get("/api/user/{id}", NOT_EXISTED_ID)
		)
				.andDo(print())
//				.andExpect(content().string(containsString("User not found")))
				.andExpect(status().isNotFound());
	}

	@Test
	void deleteWithExistedId() throws Exception {
		SecurityContextHolder.getContext().setAuthentication(accountWithoutUploadFileToken);
		given(accountService.deleteUser(DELETED_ACCOUNT_ID)).willReturn(deletedAccountResultDto);

		mockMvc.perform(
				delete("/api/users/{id}", DELETED_ACCOUNT_ID)
		)
				.andDo(print())
				.andExpect(jsonPath("id").value(DELETED_ACCOUNT_ID))
				.andExpect(jsonPath("deleted").value(true))
				.andExpect(status().isNoContent());
	}

//    @Test
//    void createWithAllValidAttributes() throws JsonProcessingException {
//        given(accountService.createUser(any(AccountCreateDto.class), any(UploadFileCreateDto.class)))
//                .will(invocation -> {
//                    AccountCreateDto accountCreateDto = invocation.getArgument(0);
//                    UploadFileCreateDto uploadFileCreateDto = invocation.getArgument(1);
//                    UploadFileResultDto uploadFileResultDto = UploadFileResultDto.builder()
//                            .id(CREATED_FILE_ID)
//                            .fileName(CREATED_FILE_NAME)
//                            .fileOriginalName(CREATED_FILE_ORIGINAL_NAME)
//                            .fileUrl(CREATED_FILE_URL)
//                            .build();
//
//                    return AccountResultDto.builder()
//                            .id(CREATED_ACCOUNT_ID)
//                            .name(accountCreateDto.getName())
//                            .email(accountCreateDto.getEmail())
//                            .nickname(accountCreateDto.getNickname())
//                            .password(accountCreateDto.getPassword())
//                            .uploadFileResultDto(uploadFileResultDto)
//                            .build();
//                });
//
//        mockMvc.perform(
//                        post("/api/users")
//                                .contentType(MediaType.APPLICATION_JSON)
//                                .content(objectMapper.writeValueAsString(accountCreateDto, uploadFileCreateDto))
//                )
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("id").value(CREATED_ACCOUNT_ID))
//                .andExpect(jsonPath("name").value(CREATED_NAME))
//                .andExpect(jsonPath("email").value(CREATED_EMAIL))
//                .andExpect(jsonPath("nickname").value(CREATED_NICKNAME))
//                .andExpect(jsonPath("password").value(CREATED_PASSWORD))
//                .andExpect(jsonPath("deleted").value(false));
//    }
//
//    @Test
//    void createWithExistedEmail() throws Exception {
//        given(accountService.createUser(any(AccountCreateDto.class)))
//                .willThrow(AccountEmailDuplicatedException.class);
//
//        mockMvc.perform(
//                post("/api/users")
//                .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(accountCreateDto))
//        )
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void createWithExistedNickname() throws Exception {
//        given(accountService.createUser(any(AccountCreateDto.class)))
//                .willThrow(AccountNicknameDuplicatedException.class);
//
//        mockMvc.perform(
//                post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(accountCreateDto))
//        )
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//
//    @Test
//    void deleteWithExistedId() throws Exception {
//        mockMvc.perform(
//                delete("/api/users/{id}", EXISTED_ID)
//        )
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
//
//    @Test
//    void updateWithValidAttribute() throws Exception {
//        mockMvc.perform(
//                patch("/api/users/{id}", EXISTED_ID)
//                .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(accountUpdateDto))
//        )
//                .andDo(print())
//                .andExpect(status().isOk());
//    }
}
