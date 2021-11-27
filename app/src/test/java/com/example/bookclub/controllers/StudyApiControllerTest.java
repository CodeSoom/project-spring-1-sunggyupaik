package com.example.bookclub.controllers;

import com.example.bookclub.application.AccountService;
import com.example.bookclub.application.StudyService;
import com.example.bookclub.controller.api.StudyApiController;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudyStartAndEndDateNotValidException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import com.example.bookclub.security.AccountAuthenticationService;
import com.example.bookclub.security.CustomDeniedHandler;
import com.example.bookclub.security.CustomEntryPoint;
import com.example.bookclub.security.UserAccount;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(StudyApiController.class)
class StudyApiControllerTest {
    private static final Long EXISTED_ID = 1L;
    private static final String SETUP_NAME = "name";
    private static final String SETUP_EMAIL = "email";
    private static final String SETUP_DESCRIPTION = "description";
    private static final String SETUP_CONTACT = "contact";
    private static final int SETUP_SIZE = 5;
    private static final LocalDate SETUP_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate SETUP_ENDDATE = LocalDate.now().plusDays(7);
    private static final Day SETUP_DAY = Day.MONDAY;
    private static final String SETUP_STARTTIME = "13:00";
    private static final String SETUP_ENDTIME = "15:30";
    private static final StudyState SETUP_STUDYSTATE = StudyState.OPEN;
    private static final Zone SETUP_ZONE = Zone.SEOUL;

    private static final String UPDATE_NAME = "updatedName";
    private static final String UPDATE_DESCRIPTION = "updatedDescription";
    private static final String UPDATE_CONTACT = "updatedContact";
    private static final int UPDATE_SIZE = 10;
    private static final LocalDate UPDATE_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate UPDATE_ENDDATE = LocalDate.now().plusDays(5);
    private static final Day UPDATE_DAY = Day.THURSDAY;
    private static final String UPDATE_STARTTIME = "12:00";
    private static final String UPDATE_ENDTIME = "14:30";
    private static final StudyState UPDATE_STUDYSTATE = StudyState.CLOSE;
    private static final Zone UPDATE_ZONE = Zone.BUSAN;

    private static final Long ACCOUNT_ID = 2L;
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_EMAIL = "email";
    private static final String ACCOUNT_NICKNAME = "accountNickname";
    private static final String ACCOUNT_PASSWORD = "accountPassword";

    private static final Long NOT_EXIST_STUDY_ID = 999L;
    private static final LocalDate CREATE_STARTDATE_PAST = LocalDate.now().minusDays(1);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private StudyService studyService;

    @MockBean
    private AccountService accountService;

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

    private Account account;
    private UsernamePasswordAuthenticationToken accountToken;
    private Study setUpStudy;
    private Study updatedStudy;
    private Study dateNotValidStudy;

    private StudyCreateDto startDateIsPastCreateDto;
    private StudyCreateDto startDateIsAfterEndDateCreateDto;

    private StudyResultDto studyResultDto;
    private StudyResultDto updatedStudyResultDto;

    private List<Study> list;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(ctx)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .alwaysDo(print())
                .build();

        account = Account.builder()
                .id(ACCOUNT_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(ACCOUNT_PASSWORD)
                .build();

        accountToken = new UsernamePasswordAuthenticationToken(
                new UserAccount(account, List.of(new SimpleGrantedAuthority("USER"))),
                account.getPassword(),
                List.of(new SimpleGrantedAuthority("USER")));

        setUpStudy = Study.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .email(SETUP_EMAIL)
                .description(SETUP_DESCRIPTION)
                .contact(SETUP_CONTACT)
                .size(SETUP_SIZE)
                .startDate(SETUP_STARTDATE)
                .endDate(SETUP_ENDDATE)
                .startTime(SETUP_STARTTIME)
                .endTime(SETUP_ENDTIME)
                .day(SETUP_DAY)
                .studyState(SETUP_STUDYSTATE)
                .zone(SETUP_ZONE)
                .build();

        updatedStudy = Study.builder()
                .id(EXISTED_ID)
                .name(UPDATE_NAME)
                .description(UPDATE_DESCRIPTION)
                .contact(UPDATE_CONTACT)
                .size(UPDATE_SIZE)
                .startDate(UPDATE_STARTDATE)
                .endDate(UPDATE_ENDDATE)
                .startTime(UPDATE_STARTTIME)
                .endTime(UPDATE_ENDTIME)
                .day(UPDATE_DAY)
                .studyState(UPDATE_STUDYSTATE)
                .zone(UPDATE_ZONE)
                .build();

        startDateIsPastCreateDto = StudyCreateDto.builder()
                .startDate(CREATE_STARTDATE_PAST)
                .endDate(SETUP_ENDDATE)
                .build();

        startDateIsAfterEndDateCreateDto = StudyCreateDto.builder()
                .startDate(SETUP_ENDDATE)
                .endDate(SETUP_STARTDATE)
                .build();

        studyResultDto = StudyResultDto.of(setUpStudy);
        updatedStudyResultDto = StudyResultDto.of(updatedStudy);

        list = List.of(setUpStudy, updatedStudy);
    }

    @Test
    void listAllStudies() throws Exception {
        given(studyService.getStudies()).willReturn(list);

        mockMvc.perform(
                get("/api/study")
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(StringContains.containsString("\"name\":\"" +  SETUP_NAME)))
                .andExpect(content().string(StringContains.containsString("\"name\":\"" + UPDATE_NAME)));
    }

    @Test
    void detailWithExistedId() throws Exception {
        given(studyService.getStudy(EXISTED_ID)).willReturn(setUpStudy);

        mockMvc.perform(
                get("/api/study/{id}", EXISTED_ID)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void detailWithNotExistedId() throws Exception {
        given(studyService.getStudy(NOT_EXIST_STUDY_ID)).willThrow(new StudyNotFoundException(NOT_EXIST_STUDY_ID));

        mockMvc.perform(
                get("/api/study/{id}", NOT_EXIST_STUDY_ID)
        )
                .andDo(print())
                .andExpect(content().string(containsString("Study not found")))
                .andExpect(status().isNotFound());

        verify(studyService).getStudy(NOT_EXIST_STUDY_ID);
    }

    @Test
    void createWithValidateAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyCreateDto.class)))
                .willReturn(studyResultDto);

        mockMvc.perform(
                post("/api/study")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(setUpStudy))
        )
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").value(studyResultDto.getId()))
                .andExpect(jsonPath("name").value(studyResultDto.getName()))
                .andExpect(jsonPath("description").value(studyResultDto.getDescription()));
    }

    @Test
    void createWithStartDateIsTodayOrBeforeInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyCreateDto.class)))
                .willThrow(new StudyStartDateInThePastException());

        mockMvc.perform(
            post("/api/study")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(startDateIsPastCreateDto))
            )
                .andDo(print())
                .andExpect(content().string(containsString("Study startDate in the past")))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithStartDateIsAfterEndDateInvalid() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountToken);
        given(studyService.createStudy(eq(ACCOUNT_EMAIL), any(StudyCreateDto.class)))
                .willThrow(new StudyStartAndEndDateNotValidException());

        mockMvc.perform(
                post("/api/study")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(startDateIsAfterEndDateCreateDto))
                )
                    .andDo(print())
                    .andExpect(content().string(containsString("Study StartDate and EndDate not valid")))
                    .andExpect(status().isBadRequest());
    }

    @Test
    void updateWithValidateAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountToken);
        given(studyService.updateStudy(any(Account.class), eq(EXISTED_ID), any(StudyUpdateDto.class)))
                .willReturn(updatedStudyResultDto);

        mockMvc.perform(
                patch("/api/study/{id}", EXISTED_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedStudy))
        )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("name").value(updatedStudyResultDto.getName()))
                .andExpect(jsonPath("description").value(updatedStudyResultDto.getDescription()))
                .andExpect(jsonPath("contact").value(updatedStudyResultDto.getContact()));
    }

    @Test
    void deleteByExistedId() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountToken);
        given(studyService.deleteStudy(any(Account.class), eq(EXISTED_ID)))
                .willReturn(studyResultDto);

        mockMvc.perform(
                delete("/api/study/{id}", EXISTED_ID)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    void applyStudyByExistedAccount() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(accountToken);
        mockMvc.perform(
                post("/api/study/apply/{id}", EXISTED_ID)
                .contentType(MediaType.APPLICATION_JSON)
        )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void cancelStudyByExistedAccount() throws Exception {
        mockMvc.perform(
                delete("/api/study/apply/{id}", EXISTED_ID)
        )
                .andDo(print())
                .andExpect(status().isNoContent());
    }
}
