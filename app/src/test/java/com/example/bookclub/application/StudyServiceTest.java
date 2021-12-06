package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyUpdateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class StudyServiceTest {
    private static final Long STUDY_SETUP_ID = 1L;
    private static final String STUDY_SETUP_NAME = "studySetupName";
    private static final String STUDY_SETUP_BOOK_NAME = "studySetupBookName";
    private static final String STUDY_SETUP_BOOK_IMAGE = "studySetupBookImage";
    private static final String STUDY_SETUP_DESCRIPTION = "studySetupDescription";
    private static final String STUDY_SETUP_CONTACT = "studySetupContact";
    private static final int STUDY_SETUP_SIZE = 5;
    private static final int STUDY_SETUP_APPLY_COUNT = 3;
    private static final LocalDate STUDY_SETUP_START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate STUDY_SETUP_END_DATE = LocalDate.now().plusDays(7);
    private static final Day STUDY_SETUP_DAY = Day.MONDAY;
    private static final String STUDY_SETUP_START_TIME = "13:00";
    private static final String STUDY_SETUP_END_TIME = "15:30";
    private static final StudyState STUDY_SETUP_STUDY_STATE = StudyState.OPEN;
    private static final Zone STUDY_SETUP_ZONE = Zone.SEOUL;

    private static final Long ACCOUNT_CREATED_MANAGER_ID = 2L;
    private static final String ACCOUNT_CREATED_MANAGER_NAME = "accountCreatedMangerName";
    private static final String ACCOUNT_CREATED_MANAGER_EMAIL = "accountCreatedManagerEmail";
    private static final String ACCOUNT_CREATED_MANAGER_NICKNAME = "accountCreatedNickName";
    private static final String ACCOUNT_CREATED_MANAGER_PASSWORD = "accountCreatedManagerPassword";

    private static final Long ACCOUNT_SETUP_MANAGER_ID = 3L;
    private static final String ACCOUNT_SETUP_MANAGER_NAME = "accountSetupManagerName";
    private static final String ACCOUNT_SETUP_MANAGER_EMAIL = "accountSetupManagerEmail";
    private static final String ACCOUNT_SETUP_MANAGER_NICKNAME = "accountSetupManagerNickname";
    private static final String ACCOUNT_SETUP_MANAGER_PASSWORD = "accountSetupManagerPassword";

    private static final Long ACCOUNT_APPLIER_ONE_ID = 4L;
    private static final String ACCOUNT_APPLIER_ONE_NAME = "accountApplierOneName";
    private static final String ACCOUNT_APPLIER_ONE_EMAIL = "accountApplierOneEmail";
    private static final String ACCOUNT_APPLIER_ONE_NICKNAME = "accountApplierOneNickname";
    private static final String ACCOUNT_APPLIER_ONE_PASSWORD = "accountApplierOnePassword";

    private static final Long STUDY_CREATED_ID = 5L;
    private static final String STUDY_CREATED_NAME = "studyCreatedName";
    private static final String STUDY_CREATED_BOOK_NAME = "studyCreatedBookName";
    private static final String STUDY_CREATED_BOOK_IMAGE = "studyCreatedBookImage";
    private static final String STUDY_CREATED_DESCRIPTION = "studyCreatedDescription";
    private static final String STUDY_CREATED_CONTACT = "studyCreatedContact";
    private static final int STUDY_CREATED_SIZE = 10;
    private static final LocalDate STUDY_CREATED_START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate STUDY_CREATED_END_DATE = LocalDate.now().plusDays(5);
    private static final Day STUDY_CREATED_DAY = Day.THURSDAY;
    private static final String STUDY_CREATED_START_TIME = "12:00";
    private static final String STUDY_CREATED_END_TIME = "14:30";
    private static final StudyState STUDY_CREATED_STUDY_STATE = StudyState.OPEN;
    private static final Zone STUDY_CREATED_ZONE = Zone.BUSAN;

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

    private List<Study> listAllStudies;
    private List<Study> listOpenedStudies;
    private List<Study> listClosedStudies;
    private List<Study> listEndedStudies;
    private List<Account> listApplierOfSetUpStudy;
    private List<Study> listPythonKeywordStudies;

	private StudyService studyService;
	private StudyRepository studyRepository;
	private AccountRepository accountRepository;
	private PasswordEncoder passwordEncoder;

	@MockBean
	private AccountService accountService;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        accountRepository = mock(AccountRepository.class);
        studyService = new StudyService(studyRepository, accountService);
        passwordEncoder = new BCryptPasswordEncoder();
        managerOfCreatedStudy = Account.builder()
                .id(ACCOUNT_CREATED_MANAGER_ID)
                .name(ACCOUNT_CREATED_MANAGER_NAME)
                .email(ACCOUNT_CREATED_MANAGER_EMAIL)
                .nickname(ACCOUNT_CREATED_MANAGER_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_CREATED_MANAGER_PASSWORD))
//                .study(createdStudy)
                .build();

        managerOfSetUpStudy = Account.builder()
                .id(ACCOUNT_SETUP_MANAGER_ID)
                .name(ACCOUNT_SETUP_MANAGER_NAME)
                .email(ACCOUNT_SETUP_MANAGER_EMAIL)
                .nickname(ACCOUNT_SETUP_MANAGER_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_SETUP_MANAGER_PASSWORD))
//                .study(setUpStudy)
                .build();

        applierOfSetUpStudyOne = Account.builder()
                .id(ACCOUNT_APPLIER_ONE_ID)
                .name(ACCOUNT_APPLIER_ONE_NAME)
                .email(ACCOUNT_APPLIER_ONE_EMAIL)
                .nickname(ACCOUNT_APPLIER_ONE_NICKNAME)
                .password(ACCOUNT_APPLIER_ONE_PASSWORD)
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
                .name(ACCOUNT_SETUP_MANAGER_NAME)
                .email(ACCOUNT_SETUP_MANAGER_EMAIL)
                .nickname(ACCOUNT_SETUP_MANAGER_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_SETUP_MANAGER_PASSWORD))
                .build();

//        listApplierOfSetUpStudy = List.of(applierOfSetUpStudyOne,
//                applierOfSetUpStudyTwo, applierOfSetUpStudyThree);
        listApplierOfSetUpStudy = new ArrayList<>();
        listApplierOfSetUpStudy.add(applierOfSetUpStudyOne);
        listApplierOfSetUpStudy.add(applierOfSetUpStudyTwo);
        listApplierOfSetUpStudy.add(applierOfSetUpStudyThree);

        setUpStudy = Study.builder()
                .id(STUDY_SETUP_ID)
                .name(STUDY_SETUP_NAME)
                .bookName(STUDY_SETUP_BOOK_NAME)
                .bookImage(STUDY_SETUP_BOOK_IMAGE)
                .email(ACCOUNT_SETUP_MANAGER_EMAIL)
                .description(STUDY_SETUP_DESCRIPTION)
                .contact(STUDY_SETUP_CONTACT)
                .size(STUDY_SETUP_SIZE)
                .applyCount(STUDY_SETUP_APPLY_COUNT)
                .startDate(STUDY_SETUP_START_DATE)
                .endDate(STUDY_SETUP_END_DATE)
                .startTime(STUDY_SETUP_START_TIME)
                .endTime(STUDY_SETUP_END_TIME)
                .day(STUDY_SETUP_DAY)
                .studyState(STUDY_SETUP_STUDY_STATE)
                .zone(STUDY_SETUP_ZONE)
                .accounts(listApplierOfSetUpStudy)
                .build();

        createdStudy = Study.builder()
                .id(STUDY_CREATED_ID)
                .name(STUDY_CREATED_NAME)
                .bookName(STUDY_CREATED_BOOK_NAME)
                .bookImage(STUDY_CREATED_BOOK_IMAGE)
                .email(ACCOUNT_CREATED_MANAGER_EMAIL)
                .description(STUDY_CREATED_DESCRIPTION)
                .contact(STUDY_CREATED_CONTACT)
                .size(STUDY_CREATED_SIZE)
                .startDate(STUDY_CREATED_START_DATE)
                .endDate(STUDY_CREATED_END_DATE)
                .startTime(STUDY_CREATED_START_TIME)
                .endTime(STUDY_CREATED_END_TIME)
                .day(STUDY_CREATED_DAY)
                .studyState(STUDY_CREATED_STUDY_STATE)
                .zone(STUDY_CREATED_ZONE)
                .build();

        fullSizeStudy = Study.builder()
                .size(STUDY_SETUP_SIZE)
                .applyCount(STUDY_SETUP_SIZE)
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
                .name(STUDY_CREATED_NAME)
                .bookName(STUDY_CREATED_BOOK_NAME)
                .bookImage(STUDY_CREATED_BOOK_IMAGE)
                //스터디가 생성될 때는 이메일 입력이 없다
//                .email(SETUP_MANAGER_EMAIL)
                .description(STUDY_CREATED_DESCRIPTION)
                .contact(STUDY_CREATED_CONTACT)
                .size(STUDY_CREATED_SIZE)
                .startDate(STUDY_CREATED_START_DATE)
                .endDate(STUDY_CREATED_END_DATE)
                .startTime(STUDY_CREATED_START_TIME)
                .endTime(STUDY_CREATED_END_TIME)
                .day(STUDY_CREATED_DAY)
                .zone(STUDY_CREATED_ZONE)
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
                .startDate(STUDY_CREATED_START_DATE)
                .endDate(STUDY_CREATED_END_DATE)
                .startTime(LATE_STARTTIME)
                .endTime(EARLY_ENDTIME)
                .build();

        //CREATED 변수들이 SETUP 변수들로 수정되었다고 가정할 것
        studyUpdateDto = StudyUpdateDto.builder()
                .name(STUDY_SETUP_NAME)
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
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(STUDY_SETUP_ID);

        assertThat(study.getId()).isEqualTo(STUDY_SETUP_ID);
    }

//    @Test
//    void detailWithNotExistedId() {
//        given(studyRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> studyService.getStudy(NOT_EXISTED_ID))
//                .isInstanceOf(StudyNotFoundException.class);
//    }
//
//    @Test
//    void createWithValidateAttribute() throws ParseException {
//        given(studyRepository.save(any(Study.class))).willReturn(createdStudy);
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//
//        StudyResultDto studyResultDto = studyService.createStudy(managerOfCreatedStudy, studyCreateDto);
//
//        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
//        assertThat(studyResultDto.getId()).isEqualTo(createdStudy.getId());
//        assertThat(studyResultDto.getName()).isEqualTo(createdStudy.getName());
//        assertThat(studyResultDto.getDescription()).isEqualTo(createdStudy.getDescription());
//        assertThat(studyResultDto.getStudyState()).isEqualTo(StudyState.OPEN);
//        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
//        assertThat(managerOfCreatedStudy.getStudy()).isEqualTo(createdStudy);
//    }
//
//    @Test
//    void createWithAccountAlreadyStudyExisted() {
//        given(accountRepository.findById(SETUP_MANAGER_ID)).willReturn(Optional.of(managerOfSetUpStudy));
//        managerOfSetUpStudy.addStudy(setUpStudy);
//
//        assertThatThrownBy(() -> studyService.createStudy(managerOfSetUpStudy, studyCreateDto))
//                .isInstanceOf(StudyAlreadyExistedException.class);
//    }
//
//    @Test
//    void createWithStartDateInThePast() {
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//
//        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, studyStartDateInThePastDto))
//                .isInstanceOf(StudyStartDateInThePastException.class);
//
//        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, studyStartDateTodayDto))
//                .isInstanceOf(StudyStartDateInThePastException.class);
//    }
//
//    @Test
//    void createWithEndDateIsBeforeStartDate() {
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//
//        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, endDateIsBeforeStartDateDto))
//                .isInstanceOf(StartAndEndDateNotValidException.class);
//    }
//
//    @Test
//    void createWithEndTimeIsBeforeStartTime() {
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//
//        assertThatThrownBy(() -> studyService.createStudy(managerOfCreatedStudy, endTimeIsBeforeStartTimeDto))
//                .isInstanceOf(StartAndEndTimeNotValidException.class);
//    }
//
//    @Test
//    void updateWithValidateAttribute() throws ParseException {
//        given(studyRepository.findById(CREATED_ID)).willReturn(Optional.of(createdStudy));
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//
//        StudyResultDto studyResultDto = studyService.updateStudy(managerOfCreatedStudy, CREATED_ID, studyUpdateDto);
//        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
//        assertThat(studyResultDto.getName()).isEqualTo(studyUpdateDto.getName());
//        assertThat(studyResultDto.getDescription()).isEqualTo(studyUpdateDto.getDescription());
//    }
//
//    @Test
//    void updateWithNullAccount() {
//        given(studyRepository.findById(CREATED_ID)).willReturn(Optional.of(createdStudy));
//        given(accountRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> studyService.updateStudy(null , CREATED_ID, studyUpdateDto))
//                .isInstanceOf(AccessDeniedException.class);
//    }
//
//    @Test
//    void updateWithInvalidAccount() {
//        given(studyRepository.findById(CREATED_ID)).willReturn(Optional.of(createdStudy));
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//
//        assertThatThrownBy(() -> studyService.updateStudy(managerOfSetUpStudy, CREATED_ID, studyUpdateDto))
//                .isInstanceOf(AccessDeniedException.class);
//    }
//
//    @Test
//    void deleteWithExistedId() {
//        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
//        given(accountRepository.findById(SETUP_MANAGER_ID)).willReturn(Optional.of(managerOfSetUpStudy));
//
//        for(Account account : listApplierOfSetUpStudy) {
//            account.addStudy(setUpStudy);
//            assertThat(account.getStudy()).isEqualTo(setUpStudy);
//        }
//        managerOfSetUpStudy.addStudy(setUpStudy);
//        assertThat(managerOfSetUpStudy.getStudy()).isEqualTo(setUpStudy);
//
//        StudyResultDto studyResultDto = studyService.deleteStudy(managerOfSetUpStudy, SETUP_ID);
//
//        assertThat(studyResultDto.getId()).isEqualTo(setUpStudy.getId());
//        List<Account> listsAfterDelete = setUpStudy.getAccounts();
//        for(Account account : listsAfterDelete) {
//            assertThat(account.getStudy()).isNull();
//        }
//        verify(studyRepository).delete(setUpStudy);
//    }
//
//    @Test
//    void applyWithValidAttribute() {
//        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
//        given(accountRepository.findById(ACCOUNT_WITHOUT_STUDY_ID))
//                .willReturn(Optional.of(accountWithoutStudy));
//
//        Study study = studyService.getStudy(SETUP_ID);
//        int beforeApplyCount = study.getApplyCount();
//        studyService.applyStudy(accountWithoutStudy, SETUP_ID);
//        int afterApplyCount = study.getApplyCount();
//        assertThat(accountWithoutStudy.getStudy()).isEqualTo(study);
//
//        assertThat(beforeApplyCount).isEqualTo(afterApplyCount - 1);
//        assertThat(setUpStudy.getAccounts()).contains(accountWithoutStudy);
//        assertThat(accountWithoutStudy.getStudy()).isNotNull();
//    }
//
//    @Test
//    void applyWithStudyAlready() {
//        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
//        given(accountRepository.findById(CREATED_MANAGER_ID)).willReturn(Optional.of(managerOfCreatedStudy));
//        managerOfCreatedStudy.addStudy(createdStudy);
//
//        assertThatThrownBy(() -> studyService.applyStudy(managerOfCreatedStudy, SETUP_ID))
//                .isInstanceOf(StudyAlreadyExistedException.class);
//    }
//
//    @Test
//    void applyWhenSizeIsFull() {
//        given(studyRepository.findById(FULL_SIZE_ID)).willReturn(Optional.of(fullSizeStudy));
//        given(accountRepository.findById(ACCOUNT_WITHOUT_STUDY_ID)).willReturn(Optional.of(accountWithoutStudy));
//
//        assertThatThrownBy(() -> studyService.applyStudy(accountWithoutStudy, FULL_SIZE_ID))
//                .isInstanceOf(StudySizeFullException.class);
//    }
//
//    @Test
//    void cancelWithValidAttribute() {
//        given(studyRepository.findById(SETUP_ID)).willReturn(Optional.of(setUpStudy));
//        given(accountRepository.findById(APPLIER_ONE_ID)).willReturn(Optional.of(applierOfSetUpStudyOne));
//        applierOfSetUpStudyOne.addStudy(setUpStudy);
//
//        Study study = studyService.getStudy(SETUP_ID);
//        int beforeApplyCount = study.getApplyCount();
//        studyService.cancelStudy(applierOfSetUpStudyOne, SETUP_ID);
//        int afterApplyCount = study.getApplyCount();
//
//        assertThat(beforeApplyCount).isEqualTo(afterApplyCount + 1);
//        assertThat(setUpStudy.getAccounts()).doesNotContain(applierOfSetUpStudyOne);
//        assertThat(applierOfSetUpStudyOne.getStudy()).isNull();
//    }
//
//    @Test
//    void listsStudiesWithKeyword() {
//        given(studyRepository.findByBookNameContaining(PYTHON_KEYWORD)).willReturn(listPythonKeywordStudies);
//
//        List<Study> list = studyService.getStudiesBySearch(PYTHON_KEYWORD);
//
//        for(Study study : list) {
//            assertThat(study.getBookName()).contains(PYTHON_KEYWORD);
//        }
//        assertThat(setUpStudy.getBookName()).doesNotContain(PYTHON_KEYWORD);
//    }
}
