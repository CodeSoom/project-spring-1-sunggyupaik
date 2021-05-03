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
import com.example.bookclub.errors.StartAndEndDateNotValidException;
import com.example.bookclub.errors.StartAndEndTimeNotValidException;
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
import static org.mockito.Mockito.verify;

public class StudyServiceTest {
    private static final Long EXISTED_ID = 1L;
    private static final String SETUP_NAME = "setUpName";
    private static final String SETUP_EMAIL = "setUpEmail";
    private static final String SETUP_BOOKNAME = "자바 세상의 빌드를 이끄는 메이븐";
    private static final String SETUP_BOOKIMAGE = "http://bimage.interpark.com/goods_image/9/4/7/8/207979478s.jpg";
    private static final String SETUP_DESCRIPTION = "setUpDescription";
    private static final String SETUP_CONTACT = "setUpContact";
    private static final int SETUP_SIZE = 5;
    private static final LocalDate SETUP_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate SETUP_ENDDATE = LocalDate.now().plusDays(7);
    private static final Day SETUP_DAY = Day.MONDAY;
    private static final String SETUP_STARTTIME = "13:00";
    private static final String SETUP_ENDTIME = "15:30";
    private static final StudyState SETUP_STUDYSTATE = StudyState.OPEN;
    private static final Zone SETUP_ZONE = Zone.SEOUL;

    private static final Long ACCOUNT_ID = 1L;
    private static final String ACCOUNT_NAME = "accountName";
    private static final String ACCOUNT_EMAIL = "accountEmail";
    private static final String ACCOUNT_NICKNAME = "accountNickName";
    private static final String ACCOUNT_PASSWORD = "1234567890";

    private static final Long MANAGER_ID = 2L;
    private static final String MANAGER_NAME = "managerName";
    private static final String MANAGER_EMAIL = "managerEmail";
    private static final String MANAGER_NICKNAME = "managerNickName";
    private static final String MANAGER_PASSWORD = "0987654321";

    private static final Long APPLIER_ONE_ID = 3L;
    private static final String APPLIER_ONE_NAME = "applierOneName";
    private static final String APPLIER_ONE_EMAIL = "applierOneEmail";
    private static final String APPLIER_ONE_NICKNAME = "applierOneNickName";
    private static final String APPLIER_ONE_PASSWORD = "11111111";

    private static final Long APPLIER_TWO_ID = 4L;
    private static final String APPLIER_TWO_NAME = "applierTwoName";
    private static final String APPLIER_TWO_EMAIL = "applierTwoEmail";
    private static final String APPLIER_TWO_NICKNAME = "applierTwoNickName";
    private static final String APPLIER_TWO_PASSWORD = "22222222";

    private static final Long APPLIER_THREE_ID = 5L;
    private static final String APPLIER_THREE_NAME = "applierThreeName";
    private static final String APPLIER_THREE_EMAIL = "applierThreeEmail";
    private static final String APPLIER_THREE_NICKNAME = "applierThreeNickName";
    private static final String APPLIER_THREE_PASSWORD = "33333333";

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

    private static final LocalDate PAST_STARTDATE = LocalDate.now().minusDays(3);
    private static final LocalDate TODAY_STARTDATE = LocalDate.now();
    private static final LocalDate LATE_STARTDATE = LocalDate.now().plusDays(5);
    private static final LocalDate EARLY_ENDDATE = LocalDate.now().plusDays(3);

    private static final String LATE_STARTTIME = "15:00";
    private static final String EARLY_ENDTIME = "13:00";

    private static final Long NOT_EXISTED_ID = 100L;
    private static final Long CREATED_ID = 3L;
    private static final Long ONELEFTSTUDY_ID = 4L;

    private Account accountWithOutStudy;
    private Account managerOfSetUpStudy;
    private Account applierOfSetUpStudyOne;
    private Account applierOfSetUpStudyTwo;
    private Account applierOfSetUpStudyThree;
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
    private StudyCreateDto endDateIsBeforeStartDateDto;
    private StudyCreateDto endTimeIsBeforeStartTimeDto;
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
    private List<Account> listApplierOfSetUpStudy;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        accountRepository = mock(AccountRepository.class);
        studyService = new StudyService(studyRepository, accountRepository);
        passwordEncoder = new BCryptPasswordEncoder();
        accountWithOutStudy = Account.builder()
                .id(ACCOUNT_ID)
                .name(ACCOUNT_NAME)
                .email(ACCOUNT_EMAIL)
                .nickname(ACCOUNT_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_PASSWORD))
                .build();

        managerOfSetUpStudy = Account.builder()
                .id(MANAGER_ID)
                .name(MANAGER_NAME)
                .email(MANAGER_EMAIL)
                .nickname(MANAGER_NICKNAME)
                .password(passwordEncoder.encode(MANAGER_PASSWORD))
                .study(setUpStudy)
                .build();

        applierOfSetUpStudyOne = Account.builder()
                .id(APPLIER_ONE_ID)
                .study(setUpStudy)
                .build();

        applierOfSetUpStudyTwo = Account.builder()
                .id(APPLIER_TWO_ID)
                .study(setUpStudy)
                .build();

        applierOfSetUpStudyThree = Account.builder()
                .id(APPLIER_THREE_ID)
                .study(setUpStudy)
                .build();

        listApplierOfSetUpStudy = List.of(applierOfSetUpStudyOne,
                applierOfSetUpStudyTwo, applierOfSetUpStudyThree);

        //addStudy를 쓰지 않고 builder로 Study 주입이 안되는 이유 확인하기
        applierOfSetUpStudyOne.addStudy(setUpStudy);
        applierOfSetUpStudyTwo.addStudy(setUpStudy);
        applierOfSetUpStudyThree.addStudy(setUpStudy);
        managerOfSetUpStudy.addStudy(setUpStudy);

        setUpStudy = Study.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .bookName(SETUP_BOOKNAME)
                .bookImage(SETUP_BOOKIMAGE)
                .email(MANAGER_EMAIL)
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
                .accounts(listApplierOfSetUpStudy)
                .build();

        createdStudy = Study.builder()
                .id(CREATED_ID)
                .name(CREATED_NAME)
                .bookName(CREATED_BOOKNAME)
                .bookImage(CREATED_BOOKIMAGE)
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
                .name(CREATED_NAME)
                .bookName(CREATED_BOOKNAME)
                .bookImage(CREATED_BOOKIMAGE)
                .email(SETUP_EMAIL)
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
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(accountWithOutStudy));

        StudyResultDto studyResultDto = studyService.createStudy(accountWithOutStudy, studyCreateDto);

        assertThat(studyResultDto.getId()).isEqualTo(createdStudy.getId());
        assertThat(studyResultDto.getName()).isEqualTo(createdStudy.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(createdStudy.getDescription());
        assertThat(studyResultDto.getStudyState()).isEqualTo(StudyState.OPEN);
        assertThat(studyResultDto.getEmail()).isEqualTo(accountWithOutStudy.getEmail());
        assertThat(accountWithOutStudy.getStudy()).isEqualTo(createdStudy);
    }

    @Test
    void createWithAccountAlreadyStudyExisted() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(managerOfSetUpStudy));

        assertThatThrownBy(() -> studyService.createStudy(managerOfSetUpStudy, studyCreateDto))
                .isInstanceOf(StudyAlreadyExistedException.class);
    }

    @Test
    void createWithStartDateInThePast() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(accountWithOutStudy));

        assertThatThrownBy(() -> studyService.createStudy(accountWithOutStudy, studyStartDateInThePastDto))
                .isInstanceOf(StudyStartDateInThePastException.class);

        assertThatThrownBy(() -> studyService.createStudy(accountWithOutStudy, studyStartDateTodayDto))
                .isInstanceOf(StudyStartDateInThePastException.class);
    }

    @Test
    void createWithEndDateIsBeforeStartDate() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(accountWithOutStudy));

        assertThatThrownBy(() -> studyService.createStudy(accountWithOutStudy, endDateIsBeforeStartDateDto))
                .isInstanceOf(StartAndEndDateNotValidException.class);
    }

    @Test
    void createWithEndTimeIsBeforeStartTime() {
        given(accountRepository.findById(ACCOUNT_ID)).willReturn(Optional.of(accountWithOutStudy));

        assertThatThrownBy(() -> studyService.createStudy(accountWithOutStudy, endTimeIsBeforeStartTimeDto))
                .isInstanceOf(StartAndEndTimeNotValidException.class);
    }

    @Test
    void updateWithValidateAttribute() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));

        StudyResultDto studyResultDto = studyService.updateStudy(accountWithOutStudy, EXISTED_ID, studyUpdateDto);
        assertThat(studyResultDto.getName()).isEqualTo(studyUpdateDto.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(studyUpdateDto.getDescription());
    }

    @Test
    void deleteWithExistedId() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(MANAGER_ID)).willReturn(Optional.of(managerOfSetUpStudy));

        for(Account account : listApplierOfSetUpStudy) {
            account.addStudy(setUpStudy);
            assertThat(account.getStudy()).isEqualTo(setUpStudy);
        }
        managerOfSetUpStudy.addStudy(setUpStudy);
        assertThat(managerOfSetUpStudy.getStudy()).isEqualTo(setUpStudy);

        StudyResultDto studyResultDto = studyService.deleteStudy(managerOfSetUpStudy, EXISTED_ID);

        assertThat(studyResultDto.getId()).isEqualTo(setUpStudy.getId());
        List<Account> listsAfterDelete = setUpStudy.getAccounts();
        for(Account account : listsAfterDelete) {
            assertThat(account.getStudy()).isNull();
        }
        verify(studyRepository).delete(setUpStudy);
    }

    @Test
    void applyWithExistedAccount() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(accountWithOutStudy));

        Study study = studyService.getStudy(EXISTED_ID);
        int beforeApplyCount = study.getApplyCount();
        Long studyId = studyService.applyStudy(accountWithOutStudy, EXISTED_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount-1);
        assertThat(setUpStudy.getAccounts()).contains(accountWithOutStudy);
        assertThat(accountWithOutStudy.getStudy()).isNotNull();
    }

    @Test
    void applyWhenSizeIsFull() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(fullSizeStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(accountWithOutStudy));

        assertThatThrownBy(() -> studyService.applyStudy(accountWithOutStudy, EXISTED_ID))
                .isInstanceOf(StudySizeFullException.class);
    }

    @Test
    void applyWhenOneLeft() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(oneLeftStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(accountWithOutStudy));

        assertThat(oneLeftStudy.getStudyState()).isEqualTo(StudyState.OPEN);
        Long studyId = studyService.applyStudy(accountWithOutStudy, EXISTED_ID);
        assertThat(oneLeftStudy.getStudyState()).isEqualTo(StudyState.CLOSE);
    }

    @Test
    void cancelWithExistedAccount() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(accountWithOutStudy));

        Study study = studyService.getStudy(EXISTED_ID);
        int beforeApplyCount = study.getApplyCount();
        Long studyId = studyService.cancelStudy(accountWithOutStudy, EXISTED_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount+1);
        assertThat(setUpStudy.getAccounts()).doesNotContain(accountWithOutStudy);
        assertThat(accountWithOutStudy.getStudy()).isNull();
    }

    @Test
    void cancelWhenFull() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(fullSizeStudy));
        given(accountRepository.findById(EXISTED_ID)).willReturn(Optional.of(accountWithOutStudy));

        assertThat(fullSizeStudy.getStudyState()).isEqualTo(StudyState.CLOSE);
        Long studyId = studyService.cancelStudy(accountWithOutStudy, EXISTED_ID);
        assertThat(fullSizeStudy.getStudyState()).isEqualTo(StudyState.OPEN);
    }
}
