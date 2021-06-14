package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.StartAndEndDateNotValidException;
import com.example.bookclub.errors.StartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class StudyServiceTest {
    private static final Long SETUP_ID = 1L;
    private static final String SETUP_NAME = "setUpName";
    private static final String SETUP_BOOKNAME = "자바 세상의 빌드를 이끄는 메이븐";
    private static final String SETUP_BOOKIMAGE = "http://bimage.interpark.com/goods_image/9/4/7/8/207979478s.jpg";
    private static final String SETUP_DESCRIPTION = "setUpDescription";
    private static final String SETUP_CONTACT = "setUpContact";
    private static final int SETUP_SIZE = 5;
    private static final int SETUP_APPLYCOUNT = 3;
    private static final LocalDate SETUP_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate SETUP_ENDDATE = LocalDate.now().plusDays(7);
    private static final Day SETUP_DAY = Day.MONDAY;
    private static final String SETUP_STARTTIME = "13:00";
    private static final String SETUP_ENDTIME = "15:30";
    private static final StudyState SETUP_STUDYSTATE = StudyState.OPEN;
    private static final Zone SETUP_ZONE = Zone.SEOUL;

    private static final Long CREATED_MANAGER_ID = 1L;
    private static final String CREATED_MANAGER_NAME = "createdMangerName";
    private static final String CREATED_MANAGER_EMAIL = "createdManagerEmail";
    private static final String CREATED_MANAGER_NICKNAME = "createdNickName";
    private static final String CREATED_MANAGER_PASSWORD = "1357924680";

    private static final Long SETUP_MANAGER_ID = 2L;
    private static final String SETUP_MANAGER_NAME = "managerName";
    private static final String SETUP_MANAGER_EMAIL = "managerEmail";
    private static final String SETUP_MANAGER_NICKNAME = "managerNickName";
    private static final String SETUP_MANAGER_PASSWORD = "0987654321";

    private static final Long APPLIER_ONE_ID = 3L;
    private static final String APPLIER_ONE_NAME = "applierOneName";
    private static final String APPLIER_ONE_EMAIL = "applierOneEmail";
    private static final String APPLIER_ONE_NICKNAME = "applierOneNickName";
    private static final String APPLIER_ONE_PASSWORD = "11111111";

    private static final Long CREATED_ID = 3L;
    private static final String CREATED_NAME = "createdName";
    private static final String CREATED_BOOKNAME = "Do it! 점프 투 파이썬";
    private static final String CREATED_BOOKIMAGE = "http://bimage.interpark.com/goods_image/7/4/8/4/310327484s.jpg";
    private static final String CREATED_DESCRIPTION = "createdDescription";
    private static final String CREATED_CONTACT = "createdContact";
    private static final int CREATED_SIZE = 10;
    private static final LocalDate CREATED_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate CREATED_ENDDATE = LocalDate.now().plusDays(5);
    private static final Day CREATED_DAY = Day.THURSDAY;
    private static final String CREATED_STARTTIME = "12:00";
    private static final String CREATED_ENDTIME = "14:30";
    private static final StudyState CREATED_STUDYSTATE = StudyState.OPEN;
    private static final Zone CREATED_ZONE = Zone.BUSAN;

    private static final String PYTHON_KEYWORD = "파이썬";
    private static final String PYTHON_BOOKNAME_ONE = "러닝 파이썬 - 상편";
    private static final String PYTHON_BOOKNAME_TWO = "파이썬 웹 프로그래밍";

    private static final LocalDate PAST_STARTDATE = LocalDate.now().minusDays(3);
    private static final LocalDate TODAY_STARTDATE = LocalDate.now();
    private static final LocalDate LATE_STARTDATE = LocalDate.now().plusDays(5);
    private static final LocalDate EARLY_ENDDATE = LocalDate.now().plusDays(3);

    private static final String LATE_STARTTIME = "15:00";
    private static final String EARLY_ENDTIME = "13:00";

    private static final Long NOT_EXISTED_ID = 100L;
    private static final Long FULL_SIZE_ID = 5L;

    private static final Long APPLIER_TWO_ID = 4L;
    private static final Long APPLIER_THREE_ID = 5L;
    private static final Long ACCOUNT_WITHOUT_STUDY_ID = 6L;

    private Account managerOfCreatedStudy;
    private Account managerOfSetUpStudy;
    private Account applierOfSetUpStudyOne;
    private Account applierOfSetUpStudyTwo;
    private Account applierOfSetUpStudyThree;
    private Account accountWithoutStudy;
    private Study setUpStudy;
    private Study createdStudy;
    private Study fullSizeStudy;
    private Study openedStudyOne;
    private Study openedStudyTwo;
    private Study closedStudyOne;
    private Study closedStudyTwo;
    private Study endedStudyOne;
    private Study endedStudyTwo;
    private Study pythonBookNameStudyOne;
    private Study pythonBookNameStudyTwo;

    private StudyCreateDto studyCreateDto;
    private StudyCreateDto studyStartDateInThePastDto;
    private StudyCreateDto studyStartDateTodayDto;
    private StudyCreateDto endDateIsBeforeStartDateDto;
    private StudyCreateDto endTimeIsBeforeStartTimeDto;
    private StudyUpdateDto studyUpdateDto;

    private StudyService studyService;
    private StudyRepository studyRepository;
    private AccountRepository accountRepository;
    private PasswordEncoder passwordEncoder;

    private List<Study> listAllStudies;
    private List<Study> listOpenedStudies;
    private List<Study> listClosedStudies;
    private List<Study> listEndedStudies;
    private List<Account> listApplierOfSetUpStudy;
    private List<Study> listPythonKeywordStudies;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        accountRepository = mock(AccountRepository.class);
        studyService = new StudyService(studyRepository);
        passwordEncoder = new BCryptPasswordEncoder();
        managerOfCreatedStudy = Account.builder()
                .id(CREATED_MANAGER_ID)
                .name(CREATED_MANAGER_NAME)
                .email(CREATED_MANAGER_EMAIL)
                .nickname(CREATED_MANAGER_NICKNAME)
                .password(passwordEncoder.encode(CREATED_MANAGER_PASSWORD))
//                .study(createdStudy)
                .build();

        managerOfSetUpStudy = Account.builder()
                .id(SETUP_MANAGER_ID)
                .name(SETUP_MANAGER_NAME)
                .email(SETUP_MANAGER_EMAIL)
                .nickname(SETUP_MANAGER_NICKNAME)
                .password(passwordEncoder.encode(SETUP_MANAGER_PASSWORD))
//                .study(setUpStudy)
                .build();

        applierOfSetUpStudyOne = Account.builder()
                .id(APPLIER_ONE_ID)
                .name(APPLIER_ONE_NAME)
                .email(APPLIER_ONE_EMAIL)
                .nickname(APPLIER_ONE_NICKNAME)
                .password(APPLIER_ONE_PASSWORD)
//                .study(setUpStudy)
                .build();

        applierOfSetUpStudyTwo = Account.builder()
                .id(APPLIER_TWO_ID)
//                .study(setUpStudy)
                .build();

        applierOfSetUpStudyThree = Account.builder()
                .id(APPLIER_THREE_ID)
//                .study(setUpStudy)
                .build();

        accountWithoutStudy = Account.builder()
                .id(ACCOUNT_WITHOUT_STUDY_ID)
                .name(SETUP_MANAGER_NAME)
                .email(SETUP_MANAGER_EMAIL)
                .nickname(SETUP_MANAGER_NICKNAME)
                .password(passwordEncoder.encode(SETUP_MANAGER_PASSWORD))
                .build();

//        listApplierOfSetUpStudy = List.of(applierOfSetUpStudyOne,
//                applierOfSetUpStudyTwo, applierOfSetUpStudyThree);
        listApplierOfSetUpStudy = new ArrayList<>();
        listApplierOfSetUpStudy.add(applierOfSetUpStudyOne);
        listApplierOfSetUpStudy.add(applierOfSetUpStudyTwo);
        listApplierOfSetUpStudy.add(applierOfSetUpStudyThree);

        setUpStudy = Study.builder()
                .id(SETUP_ID)
                .name(SETUP_NAME)
                .bookName(SETUP_BOOKNAME)
                .bookImage(SETUP_BOOKIMAGE)
                .email(SETUP_MANAGER_EMAIL)
                .description(SETUP_DESCRIPTION)
                .contact(SETUP_CONTACT)
                .size(SETUP_SIZE)
                .applyCount(SETUP_APPLYCOUNT)
                .startDate(SETUP_STARTDATE)
                .endDate(SETUP_ENDDATE)
                .startTime(SETUP_STARTTIME)
                .endTime(SETUP_ENDTIME)
                .day(SETUP_DAY)
                .studyState(SETUP_STUDYSTATE)
                .zone(SETUP_ZONE)
                .accounts(listApplierOfSetUpStudy)
                .build();

        createdStudy = Study.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .bookName(CREATED_BOOKNAME)
                .bookImage(CREATED_BOOKIMAGE)
                .email(CREATED_MANAGER_EMAIL)
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

        pythonBookNameStudyOne = Study.builder()
                .bookName(PYTHON_BOOKNAME_ONE)
                .build();

        pythonBookNameStudyTwo = Study.builder()
                .bookName(PYTHON_BOOKNAME_TWO)
                .build();

        studyCreateDto = StudyCreateDto.builder()
                .name(CREATED_NAME)
                .bookName(CREATED_BOOKNAME)
                .bookImage(CREATED_BOOKIMAGE)
                //스터디가 생성될 때는 이메일 입력이 없다
//                .email(SETUP_MANAGER_EMAIL)
                .description(CREATED_DESCRIPTION)
                .contact(CREATED_CONTACT)
                .size(CREATED_SIZE)
                .startDate(CREATED_STARTDATE)
                .endDate(CREATED_ENDDATE)
                .startTime(CREATED_STARTTIME)
                .endTime(CREATED_ENDTIME)
                .day(CREATED_DAY)
                .zone(CREATED_ZONE)
                .build();

        studyStartDateInThePastDto = StudyCreateDto.builder()
                .startDate(PAST_STARTDATE)
                .build();

        studyStartDateTodayDto = StudyCreateDto.builder()
                .startDate(TODAY_STARTDATE)
                .build();

        endDateIsBeforeStartDateDto = StudyCreateDto.builder()
                .startDate(LATE_STARTDATE)
                .endDate(EARLY_ENDDATE)
                .build();

        endTimeIsBeforeStartTimeDto = StudyCreateDto.builder()
                .startDate(CREATED_STARTDATE)
                .endDate(CREATED_ENDDATE)
                .startTime(LATE_STARTTIME)
                .endTime(EARLY_ENDTIME)
                .build();

        //CREATED 변수들이 SETUP 변수들로 수정되었다고 가정할 것
        studyUpdateDto = StudyUpdateDto.builder()
                .name(SETUP_NAME)
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

        listAllStudies = List.of(setUpStudy, createdStudy);
        listOpenedStudies = List.of(openedStudyOne, openedStudyTwo);
        listClosedStudies = List.of(closedStudyOne, closedStudyTwo);
        listEndedStudies = List.of(endedStudyOne, endedStudyTwo);
        listPythonKeywordStudies = List.of(pythonBookNameStudyOne, pythonBookNameStudyTwo);
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
        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(SETUP_ID);

        assertThat(study.getId()).isEqualTo(SETUP_ID);
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
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));

        StudyResultDto studyResultDto = studyService.createStudy(managerOfCreatedStudy, studyCreateDto);

        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
        assertThat(studyResultDto.getId()).isEqualTo(createdStudy.getId());
        assertThat(studyResultDto.getName()).isEqualTo(createdStudy.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(createdStudy.getDescription());
        assertThat(studyResultDto.getStudyState()).isEqualTo(StudyState.OPEN);
        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
        assertThat(managerOfCreatedStudy.getStudy()).isEqualTo(createdStudy);
    }

    @Test
    void createWithAccountAlreadyStudyExisted() {
        given(accountRepository.findById(SETUP_MANAGER_ID)).willReturn(Optional.of(managerOfSetUpStudy));
        managerOfSetUpStudy.addStudy(setUpStudy);

        assertThatThrownBy(() -> studyService.createStudy(managerOfSetUpStudy, studyCreateDto))
                .isInstanceOf(StudyAlreadyExistedException.class);
    }

    @Test
    void createWithStartDateInThePast() {
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));

        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, studyStartDateInThePastDto))
                .isInstanceOf(StudyStartDateInThePastException.class);

        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, studyStartDateTodayDto))
                .isInstanceOf(StudyStartDateInThePastException.class);
    }

    @Test
    void createWithEndDateIsBeforeStartDate() {
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));

        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, endDateIsBeforeStartDateDto))
                .isInstanceOf(StartAndEndDateNotValidException.class);
    }

    @Test
    void createWithEndTimeIsBeforeStartTime() {
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));

        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, endTimeIsBeforeStartTimeDto))
                .isInstanceOf(StartAndEndTimeNotValidException.class);
    }

    @Test
    void updateWithValidateAttribute() throws ParseException {
        given(studyRepository.findById(CREATED_ID)).willReturn(Optional.of(createdStudy));
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));

        StudyResultDto studyResultDto = studyService.updateStudy(managerOfCreatedStudy, CREATED_ID, studyUpdateDto);
        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
        assertThat(studyResultDto.getName()).isEqualTo(studyUpdateDto.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(studyUpdateDto.getDescription());
    }

    @Test
    void updateWithNullAccount() {
        given(studyRepository.findById(CREATED_ID)).willReturn(Optional.of(createdStudy));
        given(accountRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studyService.updateStudy(null , CREATED_ID, studyUpdateDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void updateWithInvalidAccount() {
        given(studyRepository.findById(CREATED_ID)).willReturn(Optional.of(createdStudy));
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));

        assertThatThrownBy(() -> studyService.updateStudy(managerOfSetUpStudy, CREATED_ID, studyUpdateDto))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deleteWithExistedId() {
        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(SETUP_MANAGER_ID)).willReturn(Optional.of(managerOfSetUpStudy));

        for(Account account : listApplierOfSetUpStudy) {
            account.addStudy(setUpStudy);
            assertThat(account.getStudy()).isEqualTo(setUpStudy);
        }
        managerOfSetUpStudy.addStudy(setUpStudy);
        assertThat(managerOfSetUpStudy.getStudy()).isEqualTo(setUpStudy);

        StudyResultDto studyResultDto = studyService.deleteStudy(managerOfSetUpStudy, SETUP_ID);

        assertThat(studyResultDto.getId()).isEqualTo(setUpStudy.getId());
        List<Account> listsAfterDelete = setUpStudy.getAccounts();
        for(Account account : listsAfterDelete) {
            assertThat(account.getStudy()).isNull();
        }
        verify(studyRepository).delete(setUpStudy);
    }

    @Test
    void applyWithValidAttribute() {
        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(ACCOUNT_WITHOUT_STUDY_ID))
                .willReturn(Optional.of(accountWithoutStudy));

        Study study = studyService.getStudy(SETUP_ID);
        int beforeApplyCount = study.getApplyCount();
        studyService.applyStudy(accountWithoutStudy, SETUP_ID);
        int afterApplyCount = study.getApplyCount();
        assertThat(accountWithoutStudy.getStudy()).isEqualTo(study);

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount - 1);
        assertThat(setUpStudy.getAccounts()).contains(accountWithoutStudy);
        assertThat(accountWithoutStudy.getStudy()).isNotNull();
    }

    @Test
    void applyWithStudyAlready() {
        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
        managerOfCreatedStudy.addStudy(createdStudy);

        assertThatThrownBy(() -> studyService.applyStudy(managerOfCreatedStudy, SETUP_ID))
                .isInstanceOf(StudyAlreadyExistedException.class);
    }

    @Test
    void applyWhenSizeIsFull() {
        given(studyRepository.findById(FULL_SIZE_ID)).willReturn(Optional.of(fullSizeStudy));
        given(accountRepository.findById(ACCOUNT_WITHOUT_STUDY_ID)).willReturn(Optional.of(accountWithoutStudy));

        assertThatThrownBy(() -> studyService.applyStudy(accountWithoutStudy, FULL_SIZE_ID))
                .isInstanceOf(StudySizeFullException.class);
    }

    @Test
    void cancelWithValidAttribute() {
        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(APPLIER_ONE_ID)).willReturn(Optional.of(applierOfSetUpStudyOne));
        applierOfSetUpStudyOne.addStudy(setUpStudy);

        Study study = studyService.getStudy(SETUP_ID);
        int beforeApplyCount = study.getApplyCount();
        studyService.cancelStudy(applierOfSetUpStudyOne, SETUP_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount + 1);
        assertThat(setUpStudy.getAccounts()).doesNotContain(applierOfSetUpStudyOne);
        assertThat(applierOfSetUpStudyOne.getStudy()).isNull();
    }

    @Test
    void listsStudiesWithKeyword() {
        given(studyRepository.findByBookNameContaining(PYTHON_KEYWORD)).willReturn(listPythonKeywordStudies);

        List<Study> list = studyService.getStudiesBySearch(PYTHON_KEYWORD);

        for(Study study : list) {
            assertThat(study.getBookName()).contains(PYTHON_KEYWORD);
        }
        assertThat(setUpStudy.getBookName()).doesNotContain(PYTHON_KEYWORD);
    }
}
