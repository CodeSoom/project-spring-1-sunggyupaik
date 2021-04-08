package com.example.bookclub.controllers;

import com.example.bookclub.application.StudyService;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
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
    private static final LocalDate SETUP_STARTDATE = LocalDate.now();
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

    private static final Long ACCOUNT_ID = 1L;
    private static final String ACCOUNT_NAME = "name";
    private static final String ACCOUNT_EMAIL = "email";
    private static final String ACCOUNT_NICKNAME = "nickname";
    private static final String ACCOUNT_PASSWORD = "1234567890";
    private static final String ACCOUNT_PROFILEIMAGE = "image";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext ctx;

    @MockBean
    private StudyService studyService;

    @Autowired
    private ObjectMapper objectMapper;

    private Account account;
    private UsernamePasswordAuthenticationToken token;
    private Study setUpStudy;
    private Study updatedStudy;

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
                .profileImage(ACCOUNT_PROFILEIMAGE)
                .build();

        token = new UsernamePasswordAuthenticationToken(
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
    void createWithValidateAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(token);
        given(studyService.createStudy(any(Account.class), any(StudyCreateDto.class)))
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
    void updateWithValidateAttribute() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(token);
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
        SecurityContextHolder.getContext().setAuthentication(token);
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
        SecurityContextHolder.getContext().setAuthentication(token);
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
