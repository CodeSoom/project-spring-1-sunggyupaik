package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.AccountCreateDto;
import com.example.bookclub.dto.AccountUpdateDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(AccountApiController.class)
class AccountApiControllerTest {
    private static final Long EXISTED_ID = 1L;
    private static final String SETUP_NAME = "name";
    private static final String SETUP_EMAIL = "email";
    private static final String SETUP_NICKNAME = "nickname";
    private static final String SETUP_PASSWORD = "1234567890";

    private static final Long CREATED_ID = 2L;
    private static final String CREATED_NAME = "creatName";
    private static final String CREATED_EMAIL = "createEmail";
    private static final String CREATED_NICKNAME = "createNickname";
    private static final String CREATED_PASSWORD = "0987654321";

    private static final String UPDATED_NICKNAME = "qwer";
    private static final String UPDATED_PASSWORD = "5678";

    private static final String EXISTED_EMAIL = "email";

    private Account setUpAccount;
    private Account createdAccount;

    private AccountCreateDto accountCreateDto;
    private AccountUpdateDto accountUpdateDto;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        setUpAccount = Account.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .email(SETUP_EMAIL)
                .nickname(SETUP_NICKNAME)
                .password(SETUP_PASSWORD)
                .build();

        createdAccount = Account.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        accountCreateDto = AccountCreateDto.builder()
                .name(CREATED_NAME)
                .email(CREATED_EMAIL)
                .nickname(CREATED_NICKNAME)
                .password(CREATED_PASSWORD)
                .build();

        accountUpdateDto = AccountUpdateDto.builder()
                .nickname(UPDATED_NICKNAME)
                .build();
    }

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    AccountService accountService;

//    @Test
//    void detailWithExistedId() throws Exception {
//        given(accountService.findUser(EXISTED_ID)).willReturn(setUpAccount);
//
//        mockMvc.perform(
//                get("/api/users/{id}", EXISTED_ID)
//        )
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("id").value(setUpAccount.getId()))
//                .andExpect(jsonPath("name").value(setUpAccount.getName()))
//                .andExpect(jsonPath("email").value(setUpAccount.getEmail()))
//                .andExpect(jsonPath("nickname").value(setUpAccount.getNickname()))
//                .andExpect(jsonPath("password").value(setUpAccount.getPassword()))
//                .andExpect(jsonPath("deleted").value(false));
//    }
//
//    @Test
//    void createWithValidAttribute() throws Exception {
//        given(accountService.createUser(any(AccountCreateDto.class))).will(invocation -> {
//            AccountCreateDto accountCreateDto = invocation.getArgument(0);
//            return AccountResultDto.builder()
//                    .id(CREATED_ID)
//                    .name(accountCreateDto.getName())
//                    .email(accountCreateDto.getEmail())
//                    .nickname(accountCreateDto.getNickname())
//                    .password(accountCreateDto.getPassword())
//                    .build();
//        });
//
//        mockMvc.perform(
//                post("/api/users")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(accountCreateDto))
//        )
//                .andDo(print())
//                .andExpect(status().isCreated())
//                .andExpect(jsonPath("id").value(CREATED_ID))
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
