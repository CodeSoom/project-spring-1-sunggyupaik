package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyApplyDto;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class StudyServiceTest {
    private static final Long EXISTED_ID = 1L;
    private static final String SETUP_NAME = "name";
    private static final String SETUP_EMAIL = "email";
    private static final String SETUP_BOOKNAME = "자바 세상의 빌드를 이끄는 메이븐";
    private static final String SETUP_BOOKIMAGE = "http://bimage.interpark.com/goods_image/9/4/7/8/207979478s.jpg";
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

    private static final Long ACCOUNT_ID = 1L;
    private static final String ACCOUNT_NAME = "name";
    private static final String ACCOUNT_EMAIL = "email";
    private static final String ACCOUNT_NICKNAME = "nickname";
    private static final String ACCOUNT_PASSWORD = "1234567890";

    private static final String CREATED_NAME = "updatedName";
    private static final String CREATED_DESCRIPTION = "updatedDescription";
    private static final String CREATED_CONTACT = "updatedContact";
    private static final int CREATED_SIZE = 10;
    private static final LocalDate CREATED_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate CREATED_ENDDATE = LocalDate.now().plusDays(5);
    private static final Day CREATED_DAY = Day.THURSDAY;
    private static final String CREATED_STARTTIME = "12:00";
    private static final String CREATED_ENDTIME = "14:30";
    private static final StudyState CREATED_STUDYSTATE = StudyState.OPEN;
    private static final Zone CREATED_ZONE = Zone.BUSAN;

    private static final LocalDate PAST_STARTDATE = LocalDate.now().minusDays(3);
    private static final LocalDate TODAY_STARTDATE = LocalDate.now();

    private static final Long NOT_EXISTED_ID = 2L;
    private static final Long CREATED_ID = 3L;
    private static final Long ONELEFTSTUDY_ID = 4L;

    private Account account;
    private Account accountWithStudy;
    private Study setUpStudy;
    private Study createdStudy;
    private Study fullSizeStudy;
    private Study oneLeftStudy;
    private Study openedStudyOne;
    private Study openedStudyTwo;
    private Study closedStudyOne;
    private Study closedStudyTwo;
    private Study endedStudyOne;
    private Study endedStudyTwo;

    private StudyCreateDto studyCreateDto;
    private StudyCreateDto studyStartDateInThePastDto;
    private StudyCreateDto studyStartDateTodayDto;
    private StudyUpdateDto studyUpdateDto;

    private StudyApplyDto studyApplyDto;

    private StudyService studyService;
    private StudyRepository studyRepository;
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    private List<Study> listAllStudies;
    private List<Study> listOpenedStudies;
    private List<Study> listClosedStudies;
    private List<Study> listEndedStudies;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        accountRepository = mock(AccountRepository.class);
        studyService = new StudyService(studyRepository, accountRepository);
        passwordEncoder = new BCryptPasswordEncoder();
        account = Account.builder()
                .id(ACCOUNT_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_PASSWORD))
                .build();

        setUpStudy = Study.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .bookName(SETUP_BOOKNAME)
                .bookImage(SETUP_BOOKIMAGE)
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

        accountWithStudy = Account.builder()
                .id(ACCOUNT_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_PASSWORD))
                .study(setUpStudy)
                .build();

        createdStudy = Study.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .email(SETUP_EMAIL)
                .description(CREATED_DESCRIPTION)
                .contact(CREATED_CONTACT)
                .size(CREATED_SIZE)
                .startDate(CREATED_STARTDATE)
                .endDate(CREATED_ENDDATE)
                .startTime(CREATED_STARTTIME)
                .endTime(CREATED_ENDTIME)
                .day(CREATED_DAY)
                .studyState(CREATED_STUDYSTATE)
                .zone(CREATED_ZONE)
                .build();

        fullSizeStudy = Study.builder()
                .size(SETUP_SIZE)
                .applyCount(SETUP_SIZE)
                .studyState(StudyState.CLOSE)
                .build();

        oneLeftStudy = Study.builder()
                .id(ONELEFTSTUDY_ID)
                .size(SETUP_SIZE)
                .applyCount(SETUP_SIZE-1)
                .studyState(StudyState.OPEN)
                .build();

        openedStudyOne = Study.builder()
                .studyState(StudyState.OPEN)
                .build();

        openedStudyTwo = Study.builder()
                .studyState(StudyState.OPEN)
                .build();

        closedStudyOne = Study.builder()
                .studyState(StudyState.CLOSE)
                .build();

        closedStudyTwo = Study.builder()
                .studyState(StudyState.CLOSE)
                .build();

        endedStudyOne = Study.builder()
                .studyState(StudyState.END)
                .build();

        endedStudyTwo = Study.builder()
                .studyState(StudyState.END)
                .build();

        studyCreateDto = StudyCreateDto.builder()
                .name(SETUP_NAME)
                .bookName(SETUP_BOOKNAME)
                .bookImage(SETUP_BOOKIMAGE)
                .email(SETUP_EMAIL)
                .description(SETUP_DESCRIPTION)
                .contact(SETUP_CONTACT)
                .size(SETUP_SIZE)
                .startDate(SETUP_STARTDATE)
                .endDate(SETUP_ENDDATE)
                .startTime(SETUP_STARTTIME)
                .endTime(SETUP_ENDTIME)
                .day(SETUP_DAY)
                .zone(SETUP_ZONE)
                .build();

        studyStartDateInThePastDto = StudyCreateDto.builder()
                .startDate(PAST_STARTDATE)
                .build();

        studyStartDateTodayDto = StudyCreateDto.builder()
                .startDate(TODAY_STARTDATE)
                .build();

        studyUpdateDto = StudyUpdateDto.builder()
                //업데이트 테스트에서 변수에 값을 할당할 것
//                .name(UPDATE_NAME)
//                .description(UPDATE_DESCRIPTION)
//                .contact(UPDATE_CONTACT)
//                .size(UPDATE_SIZE)
//                .startDate(UPDATE_STARTDATE)
//                .endDate(UPDATE_ENDDATE)
//                .startTime(UPDATE_STARTTIME)
//                .endTime(UPDATE_ENDTIME)
//                .day(UPDATE_DAY)
//                .studyState(UPDATE_STUDYSTATE)
//                .zone(UPDATE_ZONE)
                .build();

        studyApplyDto = StudyApplyDto.builder()
                .email(SETUP_EMAIL)
                .build();

        listAllStudies = List.of(setUpStudy, createdStudy);
        listOpenedStudies = List.of(openedStudyOne, openedStudyTwo);
        listClosedStudies = List.of(closedStudyOne, closedStudyTwo);
        listEndedStudies = List.of(endedStudyOne, endedStudyTwo);
    }

    @Test
    void listAllStudies() {
        given(studyRepository.findAll()).willReturn(listAllStudies);

        List<Study> lists = studyService.getStudies();

        assertThat(lists).containsExactly(setUpStudy, createdStudy);
    }

    @Test
    void listOpenedStudies() {
        given(studyRepository.findByStudyState(StudyState.OPEN)).willReturn(listOpenedStudies);

        List<Study> lists = studyService.getStudiesByStudyState(StudyState.OPEN);

        for(Study study : lists) {
            assertThat(study.getStudyState()).isEqualTo(StudyState.OPEN);
        }
    }

    @Test
    void listClosedStudies() {
        given(studyRepository.findByStudyState(StudyState.CLOSE)).willReturn(listClosedStudies);

        List<Study> lists = studyService.getStudiesByStudyState(StudyState.CLOSE);

        for(Study study : lists) {
            assertThat(study.getStudyState()).isEqualTo(StudyState.CLOSE);
        }
    }

    @Test
    void listEndedStudies() {
        given(studyRepository.findByStudyState(StudyState.END)).willReturn(listEndedStudies);

        List<Study> lists = studyService.getStudiesByStudyState(StudyState.END);

        for(Study study : lists) {
            assertThat(study.getStudyState()).isEqualTo(StudyState.END);
        }
    }

    @Test
    void detailWithExistedId() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(EXISTED_ID);

        assertThat(study.getId()).isEqualTo(EXISTED_ID);
    }

    @Test
    void detailWithNotExistedId() {
        given(studyRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studyService.getStudy(NOT_EXISTED_ID))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    void createWithValidateAttribute() throws ParseException {
        given(studyRepository.save(any(Study.class))).willReturn(createdStudy);
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(account));

        StudyResultDto studyResultDto = studyService.createStudy(account, studyCreateDto);

        assertThat(studyResultDto.getId()).isEqualTo(createdStudy.getId());
        assertThat(studyResultDto.getName()).isEqualTo(createdStudy.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(createdStudy.getDescription());
        assertThat(studyResultDto.getStudyState()).isEqualTo(StudyState.OPEN);
    }

    @Test
    void createWithAccountAlreadyStudyExisted() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(accountWithStudy));

        assertThatThrownBy(() -> studyService.createStudy(accountWithStudy, studyCreateDto))
                .isInstanceOf(StudyAlreadyExistedException.class);
    }

    @Test
    void createWithStartDateInThePast() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(account));

        assertThatThrownBy(() -> studyService.createStudy(account, studyStartDateInThePastDto))
                .isInstanceOf(StudyStartDateInThePastException.class);

        assertThatThrownBy(() -> studyService.createStudy(account, studyStartDateTodayDto))
                .isInstanceOf(StudyStartDateInThePastException.class);
    }

    @Test
    void updateWithValidateAttribute() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));

        StudyResultDto studyResultDto = studyService.updateStudy(account, EXISTED_ID, studyUpdateDto);
        assertThat(studyResultDto.getName()).isEqualTo(studyUpdateDto.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(studyUpdateDto.getDescription());
    }

    @Test
    void deleteWithExistedId() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));

        StudyResultDto studyResultDto = studyService.deleteStudy(account, EXISTED_ID);

        assertThat(studyResultDto.getName()).isEqualTo(setUpStudy.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(setUpStudy.getDescription());
    }

    @Test
    void applyWithExistedAccount() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(account));

        Study study = studyService.getStudy(EXISTED_ID);
        int beforeApplyCount = study.getApplyCount();
        Long studyId = studyService.applyStudy(account, EXISTED_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount-1);
        assertThat(setUpStudy.getAccounts()).contains(account);
        assertThat(account.getStudy()).isNotNull();
    }

    @Test
    void applyWhenSizeIsFull() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(fullSizeStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(account));

        assertThatThrownBy(() -> studyService.applyStudy(account, EXISTED_ID))
                .isInstanceOf(StudySizeFullException.class);
    }

    @Test
    void applyWhenOneLeft() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(oneLeftStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(account));

        assertThat(oneLeftStudy.getStudyState()).isEqualTo(StudyState.OPEN);
        Long studyId = studyService.applyStudy(account, EXISTED_ID);
        assertThat(oneLeftStudy.getStudyState()).isEqualTo(StudyState.CLOSE);
    }

    @Test
    void cancelWithExistedAccount() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(account));

        Study study = studyService.getStudy(EXISTED_ID);
        int beforeApplyCount = study.getApplyCount();
        Long studyId = studyService.cancelStudy(account, EXISTED_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount+1);
        assertThat(setUpStudy.getAccounts()).doesNotContain(account);
        assertThat(account.getStudy()).isNull();
    }

    @Test
    void cancelWhenFull() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(fullSizeStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(account));

        assertThat(fullSizeStudy.getStudyState()).isEqualTo(StudyState.CLOSE);
        Long studyId = studyService.cancelStudy(account, EXISTED_ID);
        assertThat(fullSizeStudy.getStudyState()).isEqualTo(StudyState.OPEN);
    }
}
