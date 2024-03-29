package com.example.bookclub.application;

import com.amazonaws.services.s3.AmazonS3;
import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.study.StudyService;
import com.example.bookclub.application.uploadfile.UploadFileService;
import com.example.bookclub.common.exception.account.AccountNotManagerOfStudyException;
import com.example.bookclub.common.exception.study.StudyAlreadyExistedException;
import com.example.bookclub.common.exception.study.StudyAlreadyInOpenOrCloseException;
import com.example.bookclub.common.exception.study.StudyNotAppliedBefore;
import com.example.bookclub.common.exception.study.StudyNotFoundException;
import com.example.bookclub.common.exception.study.StudyNotInOpenStateException;
import com.example.bookclub.common.exception.study.StudySizeFullException;
import com.example.bookclub.common.exception.study.StudyStartAndEndDateNotValidException;
import com.example.bookclub.common.exception.study.StudyStartAndEndTimeNotValidException;
import com.example.bookclub.common.exception.study.StudyStartDateInThePastException;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.account.emailauthentication.EmailAuthenticationRepository;
import com.example.bookclub.domain.account.role.RoleRepository;
import com.example.bookclub.domain.study.Day;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudySeriesFactory;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.domain.study.Zone;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.infrastructure.account.JpaAccountRepository;
import com.example.bookclub.infrastructure.study.JpaStudyRepository;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
    private static final Long STUDY_SETUP_ID = 1L;
    private static final String STUDY_SETUP_NAME = "studySetupName";
    private static final String STUDY_SETUP_BOOK_NAME = "studySetupBookName";
    private static final String STUDY_SETUP_BOOK_IMAGE = "studySetupBookImage";
    private static final String STUDY_SETUP_DESCRIPTION = "studySetupDescription";
    private static final String STUDY_SETUP_CONTACT = "studySetupContact";
    private static final int STUDY_SETUP_SIZE = 5;
    private static final int STUDY_SETUP_APPLY_COUNT = 1;
    private static final LocalDate STUDY_SETUP_START_DATE = LocalDate.now().plusDays(1);
    private static final LocalDate STUDY_SETUP_END_DATE = LocalDate.now().plusDays(7);
    private static final Day STUDY_SETUP_DAY = Day.MONDAY;
    private static final String STUDY_SETUP_START_TIME = "13:00";
    private static final String STUDY_SETUP_END_TIME = "15:30";
    private static final StudyState STUDY_SETUP_STUDY_STATE = StudyState.OPEN;
    private static final Zone STUDY_SETUP_ZONE = Zone.SEOUL;

	private static final Long ACCOUNT_SETUP_MANAGER_ID = 2L;
	private static final String ACCOUNT_SETUP_MANAGER_NAME = "accountSetupManagerName";
	private static final String ACCOUNT_SETUP_MANAGER_EMAIL = "accountSetupManagerEmail";
	private static final String ACCOUNT_SETUP_MANAGER_NICKNAME = "accountSetupManagerNickname";
	private static final String ACCOUNT_SETUP_MANAGER_PASSWORD = "accountSetupManagerPassword";

    private static final Long ACCOUNT_CREATED_STUDY_ID = 3L;
    private static final String ACCOUNT_CREATED_NAME = "accountCreatedName";
    private static final String ACCOUNT_CREATED_STUDY_EMAIL = "accountCreatedStudyEmail";
    private static final String ACCOUNT_CREATED_NICKNAME = "accountCreatedNickName";
    private static final String ACCOUNT_CREATED_PASSWORD = "accountCreatedManagerPassword";

    private static final Long ACCOUNT_APPLIER_ONE_ID = 4L;
	private static final Long ACCOUNT_APPLIER_TWO_ID = 5L;
	private static final Long ACCOUNT_APPLIER_THREE_ID = 6L;
	private static final Long ACCOUNT_APPLIER_WITHOUT_STUDY_ID = 9L;

    private static final Long STUDY_CREATED_ID = 7L;
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

    private static final String BOOK_PYTHON_KEYWORD = "파이썬";
    private static final String BOOK_NAME_PYTHON_ONE = "러닝 파이썬 - 상편";
    private static final String BOOK_NAME_PYTHON_TWO = "파이썬 웹 프로그래밍";

    private static final LocalDate STUDY_PAST_START_DATE = LocalDate.now().minusDays(3);
    private static final LocalDate STUDY_TODAY_START_DATE = LocalDate.now();
    private static final LocalDate STUDY_LATE_START_DATE = LocalDate.now().plusDays(5);
    private static final LocalDate STUDY_EARLY_END_DATE = LocalDate.now().plusDays(3);
	private static final LocalDate TODAY = LocalDate.now();
	private static final LocalDate YESTERDAY = LocalDate.now().minusDays(1);
	private static final LocalDate TOMORROW = LocalDate.now().plusDays(1);

    private static final String STUDY_LATE_START_TIME = "15:00";
    private static final String STUDY_EARLY_END_TIME = "13:00";

    private static final Long STUDY_NOT_EXISTED_ID = 100L;
    private static final Long STUDY_FULL_SIZE_ID = 8L;
	private static final Long STUDY_CLOSED_ID = 9L;

    private static final Long ACCOUNT_CREATED_WITHOUT_STUDY_ID = ACCOUNT_CREATED_STUDY_ID;

    private Account managerOfCreatedStudy;
    private Account managerOfSetUpStudy;
	private Account accountWithoutStudy;
    private Account applierOfSetUpStudyOne;
    private Account applierOfSetUpStudyTwo;
    private Account applierOfSetUpStudyThree;
    private Account accountCreatedWithoutStudy;
	private UserAccount userAccountWithoutStudy;
	private UserAccount userAccountManagerOfCreatedStudy;
	private UserAccount userAccountApplierOneOfSetUpStudy;

    private Study setUpStudy;
    private Study createdStudy;
    private Study fullSizeStudy;
    private Study startTodayOpenedStudyOne;
    private Study startTomorrowOpenedStudyTwo;
    private Study endTodayClosedStudyOne;
    private Study endYesterdayClosedStudyTwo;
    private Study endedStudyOne;
    private Study endedStudyTwo;
	private Study openedStudyWithOpenKeyword;
	private Study closedStudyWithCloseKeyword;
	private Study endedStudyWithEndKeyword;
    private Study bookNamePythonStudyOne;
    private Study bookNamePythonStudyTwo;
	private Study closedStudy;

    private StudyApiDto.StudyCreateDto studyCreateDto;
    private StudyApiDto.StudyCreateDto studyStartDateInThePastCreateDto;
    private StudyApiDto.StudyCreateDto studyStartDateTodayCreateDto;
    private StudyApiDto.StudyCreateDto endDateIsBeforeStartDateCreateDto;
    private StudyApiDto.StudyCreateDto endTimeIsBeforeStartTimeCreateDto;
    private StudyApiDto.StudyUpdateDto studyUpdateDto;
	private StudyApiDto.StudyUpdateDto studyStartDateInThePastUpdateDto;
	private StudyApiDto.StudyUpdateDto studyStartDateTodayUpdateDto;
	private StudyApiDto.StudyUpdateDto endDateIsBeforeStartDateUpdateDto;
	private StudyApiDto.StudyUpdateDto endTimeIsBeforeStartTimeUpdateDto;

    private List<Study> listAllStudies;
    private List<Study> listOpenedStudies;
    private List<Study> listClosedStudies;
    private List<Study> listEndedStudies;
	private List<Study> listOpenedStudiesWithOpenKeyword;
	private List<Study> listClosedStudiesWithCloseKeyword;
	private List<Study> listEndedStudiesWithEndKeyword;
    private List<Account> listApplierOfSetUpStudy;
    private List<Study> listBookNamePythonKeywordStudies;

	private StudyService studyService;
	private JpaStudyRepository studyRepository;
	private JpaAccountRepository accountRepository;
	private PasswordEncoder passwordEncoder;
	private EmailAuthenticationRepository emailAuthenticationRepository;
	private RoleRepository roleRepository;
	private AmazonS3 amazonS3;
	private UploadFileService uploadFileService;
	private AccountService accountService;
	private StudySeriesFactory studySeriesFactory;

    @BeforeEach
    void setUp() {
        studyRepository = mock(JpaStudyRepository.class);
        accountRepository = mock(JpaAccountRepository.class);
		studySeriesFactory = mock(StudySeriesFactory.class);
		emailAuthenticationRepository = mock(EmailAuthenticationRepository.class);
		roleRepository = mock(RoleRepository.class);
		amazonS3 = mock(AmazonS3.class);
		uploadFileService = new UploadFileService(amazonS3);
		passwordEncoder = new BCryptPasswordEncoder();
		accountService = new AccountService(accountRepository, emailAuthenticationRepository,
				                            passwordEncoder, uploadFileService, roleRepository);
        studyService = new StudyService(studyRepository, accountService, studySeriesFactory);

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
				.build();

		createdStudy = Study.builder()
				.id(STUDY_CREATED_ID)
				.name(STUDY_CREATED_NAME)
				.bookName(STUDY_CREATED_BOOK_NAME)
				.bookImage(STUDY_CREATED_BOOK_IMAGE)
				.email(ACCOUNT_CREATED_STUDY_EMAIL)
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

		closedStudy = Study.builder()
				.id(STUDY_CLOSED_ID)
				.studyState(StudyState.CLOSE)
				.build();

		managerOfSetUpStudy = Account.builder()
				.id(ACCOUNT_SETUP_MANAGER_ID)
				.name(ACCOUNT_SETUP_MANAGER_NAME)
				.email(ACCOUNT_SETUP_MANAGER_EMAIL)
				.nickname(ACCOUNT_SETUP_MANAGER_NICKNAME)
				.password(passwordEncoder.encode(ACCOUNT_SETUP_MANAGER_PASSWORD))
				.build();

		setUpStudy.addAdmin(managerOfSetUpStudy);

		managerOfCreatedStudy = Account.builder()
				.id(ACCOUNT_CREATED_STUDY_ID)
				.name(ACCOUNT_CREATED_NAME)
				.email(ACCOUNT_CREATED_STUDY_EMAIL)
				.nickname(ACCOUNT_CREATED_NICKNAME)
				.password(passwordEncoder.encode(ACCOUNT_CREATED_PASSWORD))
				.build();

		createdStudy.addAdmin(managerOfCreatedStudy);

        applierOfSetUpStudyOne = Account.builder()
                .id(ACCOUNT_APPLIER_ONE_ID)
                .build();

		setUpStudy.addAccount(applierOfSetUpStudyOne);
        applierOfSetUpStudyTwo = Account.builder()
                .id(ACCOUNT_APPLIER_TWO_ID)
                .build();

		setUpStudy.addAccount(applierOfSetUpStudyTwo);
        applierOfSetUpStudyThree = Account.builder()
                .id(ACCOUNT_APPLIER_THREE_ID)
                .build();

		setUpStudy.addAccount(applierOfSetUpStudyThree);
		accountWithoutStudy = Account.builder()
				.id(ACCOUNT_APPLIER_WITHOUT_STUDY_ID)
				.build();

        accountCreatedWithoutStudy = Account.builder()
                .id(ACCOUNT_CREATED_WITHOUT_STUDY_ID)
                .name(ACCOUNT_CREATED_NAME)
                .email(ACCOUNT_CREATED_STUDY_EMAIL)
                .nickname(ACCOUNT_CREATED_NICKNAME)
                .password(passwordEncoder.encode(ACCOUNT_CREATED_PASSWORD))
                .build();

        listApplierOfSetUpStudy = List.of(applierOfSetUpStudyOne,
                applierOfSetUpStudyTwo, applierOfSetUpStudyThree);

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));

		userAccountWithoutStudy = UserAccount.builder()
				.account(accountWithoutStudy)
				.authorities(authorities)
				.build();

		userAccountManagerOfCreatedStudy = UserAccount.builder()
				.account(managerOfCreatedStudy)
				.authorities(authorities)
				.build();

		userAccountApplierOneOfSetUpStudy = UserAccount.builder()
				.account(applierOfSetUpStudyOne)
				.authorities(authorities)
				.build();

        fullSizeStudy = Study.builder()
                .size(STUDY_SETUP_SIZE)
                .applyCount(STUDY_SETUP_SIZE)
                .studyState(StudyState.OPEN)
                .build();

		startTodayOpenedStudyOne = Study.builder()
                .studyState(StudyState.OPEN)
				.startDate(TODAY)
                .build();

        startTomorrowOpenedStudyTwo = Study.builder()
                .studyState(StudyState.OPEN)
				.startDate(TOMORROW)
                .build();

        endTodayClosedStudyOne = Study.builder()
                .studyState(StudyState.CLOSE)
				.endDate(TODAY)
                .build();

		openedStudyWithOpenKeyword = Study.builder()
				.studyState(StudyState.OPEN)
				.name("Open")
				.build();

		closedStudyWithCloseKeyword = Study.builder()
				.studyState(StudyState.CLOSE)
				.name("Close")
				.build();

		endedStudyWithEndKeyword = Study.builder()
				.studyState(StudyState.END)
				.name("End")
				.build();

        endYesterdayClosedStudyTwo = Study.builder()
                .studyState(StudyState.CLOSE)
				.endDate(YESTERDAY)
                .build();

        endedStudyOne = Study.builder()
                .studyState(StudyState.END)
                .build();

        endedStudyTwo = Study.builder()
                .studyState(StudyState.END)
                .build();

        bookNamePythonStudyOne = Study.builder()
                .bookName(BOOK_NAME_PYTHON_ONE)
                .build();

        bookNamePythonStudyTwo = Study.builder()
                .bookName(BOOK_NAME_PYTHON_TWO)
                .build();

        studyCreateDto = StudyApiDto.StudyCreateDto.builder()
                .name(STUDY_CREATED_NAME)
                .bookName(STUDY_CREATED_BOOK_NAME)
                .bookImage(STUDY_CREATED_BOOK_IMAGE)
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

        studyStartDateInThePastCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startDate(STUDY_PAST_START_DATE)
                .build();

        studyStartDateTodayCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startDate(STUDY_TODAY_START_DATE)
                .build();

        endDateIsBeforeStartDateCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startDate(STUDY_LATE_START_DATE)
                .endDate(STUDY_EARLY_END_DATE)
                .build();

        endTimeIsBeforeStartTimeCreateDto = StudyApiDto.StudyCreateDto.builder()
                .startDate(STUDY_CREATED_START_DATE)
                .endDate(STUDY_CREATED_END_DATE)
                .startTime(STUDY_LATE_START_TIME)
                .endTime(STUDY_EARLY_END_TIME)
                .build();

        //CREATED 변수들이 SETUP 변수들로 수정되었다고 가정할 것
        studyUpdateDto = StudyApiDto.StudyUpdateDto.builder()
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

		studyStartDateInThePastUpdateDto = StudyApiDto.StudyUpdateDto.builder()
				.startDate(STUDY_PAST_START_DATE)
				.build();

		studyStartDateTodayUpdateDto = StudyApiDto.StudyUpdateDto.builder()
				.startDate(STUDY_TODAY_START_DATE)
				.build();

		endDateIsBeforeStartDateUpdateDto = StudyApiDto.StudyUpdateDto.builder()
				.startDate(STUDY_LATE_START_DATE)
				.endDate(STUDY_EARLY_END_DATE)
				.build();

		endTimeIsBeforeStartTimeUpdateDto = StudyApiDto.StudyUpdateDto.builder()
				.startDate(STUDY_CREATED_START_DATE)
				.endDate(STUDY_CREATED_END_DATE)
				.startTime(STUDY_LATE_START_TIME)
				.endTime(STUDY_EARLY_END_TIME)
				.build();

        listAllStudies = List.of(setUpStudy, createdStudy);
        listOpenedStudies = List.of(startTodayOpenedStudyOne, startTomorrowOpenedStudyTwo);
        listClosedStudies = List.of(endTodayClosedStudyOne, endYesterdayClosedStudyTwo);
        listEndedStudies = List.of(endedStudyOne, endedStudyTwo);
		listOpenedStudiesWithOpenKeyword = List.of(openedStudyWithOpenKeyword);
		listClosedStudiesWithCloseKeyword = List.of(closedStudyWithCloseKeyword);
		listEndedStudiesWithEndKeyword = List.of(endedStudyWithEndKeyword);
        listBookNamePythonKeywordStudies = List.of(bookNamePythonStudyOne, bookNamePythonStudyTwo);
    }

    @Test
    void listAllStudies() {
        given(studyRepository.findAll()).willReturn(listAllStudies);

        List<Study> lists = studyService.getStudies();

        assertThat(lists).containsExactly(setUpStudy, createdStudy);
    }

	@Test
	void listOpenedStudies() {
		Pageable pageable = PageRequest.of(0, 5);
		given(studyRepository.findByBookNameContaining("", StudyState.OPEN, pageable)).willReturn(listOpenedStudies);

		List<StudyApiDto.StudyResultDto> lists = studyService
				.getStudiesBySearch("", StudyState.OPEN, accountWithoutStudy, pageable);

		for(StudyApiDto.StudyResultDto study : lists) {
			assertThat(study.getStudyState()).isEqualTo(StudyState.OPEN);
		}
	}

	@Test
	void listClosedStudies() {
		Pageable pageable = PageRequest.of(0, 5);
		given(studyRepository.findByBookNameContaining("", StudyState.CLOSE, pageable)).willReturn(listClosedStudies);

		List<StudyApiDto.StudyResultDto> lists = studyService
				.getStudiesBySearch("", StudyState.CLOSE, accountWithoutStudy, pageable);

		for(StudyApiDto.StudyResultDto study : lists) {
			assertThat(study.getStudyState()).isEqualTo(StudyState.CLOSE);
		}
	}

	@Test
	void listEndedStudies() {
		Pageable pageable = PageRequest.of(0, 5);
		given(studyRepository.findByBookNameContaining("", StudyState.END, pageable)).willReturn(listEndedStudies);

		List<StudyApiDto.StudyResultDto> lists = studyService
				.getStudiesBySearch("", StudyState.END, accountWithoutStudy, pageable);

		for(StudyApiDto.StudyResultDto study : lists) {
			assertThat(study.getStudyState()).isEqualTo(StudyState.END);
		}
	}

	@Test
	void listOpenedStudiesWithKeyword() {
		Pageable pageable = PageRequest.of(0, 5);
		given(studyRepository.findByBookNameContaining("Open", StudyState.OPEN, pageable))
				.willReturn(listOpenedStudies);

		List<StudyApiDto.StudyResultDto> lists = studyService
				.getStudiesBySearch("Open", StudyState.OPEN, accountWithoutStudy, pageable);

		for(StudyApiDto.StudyResultDto study : lists) {
			assertThat(study.getStudyState()).isEqualTo(StudyState.OPEN);
			assertThat(study.getName()).isEqualTo("Open");
		}
	}

	@Test
	void listClosedStudiesWithKeyword() {
		Pageable pageable = PageRequest.of(0, 5);
		given(studyRepository.findByBookNameContaining("Close", StudyState.OPEN, pageable))
				.willReturn(listOpenedStudies);

		List<StudyApiDto.StudyResultDto> lists = studyService
				.getStudiesBySearch("Close", StudyState.OPEN, accountWithoutStudy, pageable);

		for(StudyApiDto.StudyResultDto study : lists) {
			assertThat(study.getStudyState()).isEqualTo(StudyState.OPEN);
			assertThat(study.getName()).isEqualTo("Close");
		}
	}

	@Test
	void listEndedStudiesWithKeyword() {
		Pageable pageable = PageRequest.of(0, 5);
		given(studyRepository.findByBookNameContaining("End", StudyState.OPEN, pageable))
				.willReturn(listOpenedStudies);

		List<StudyApiDto.StudyResultDto> lists = studyService
				.getStudiesBySearch("End", StudyState.OPEN, accountWithoutStudy, pageable);

		for(StudyApiDto.StudyResultDto study : lists) {
			assertThat(study.getStudyState()).isEqualTo(StudyState.OPEN);
			assertThat(study.getName()).isEqualTo("End");
		}
	}

    @Test
    void detailWithExistedId() {
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(STUDY_SETUP_ID);

        assertThat(study.getId()).isEqualTo(STUDY_SETUP_ID);
    }

    @Test
    void detailWithNotExistedId() {
        given(studyRepository.findById(STUDY_NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studyService.getStudy(STUDY_NOT_EXISTED_ID))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    void createWithValidateAttribute() {
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(accountCreatedWithoutStudy));
		given(studyRepository.save(any(Study.class))).willReturn(createdStudy);

		StudyApiDto.StudyResultDto studyResultDto = studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, studyCreateDto);

		assertThat(studyResultDto.getId()).isEqualTo(STUDY_CREATED_ID);
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
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(managerOfCreatedStudy));

        assertThatThrownBy(
				() -> studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, studyCreateDto)
		)
                .isInstanceOf(StudyAlreadyInOpenOrCloseException.class);
    }

    @Test
    void createWithStartDateInThePast() {
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(accountCreatedWithoutStudy));

        assertThatThrownBy(
				() -> studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, studyStartDateInThePastCreateDto)
		)
                .isInstanceOf(StudyStartDateInThePastException.class);

		assertThatThrownBy(
				() -> studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, studyStartDateTodayCreateDto)
		)
                .isInstanceOf(StudyStartDateInThePastException.class);
    }

    @Test
    void createWithEndDateIsBeforeStartDate() {
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(accountCreatedWithoutStudy));

        assertThatThrownBy(
				() -> studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, endDateIsBeforeStartDateCreateDto)
		)
                .isInstanceOf(StudyStartAndEndDateNotValidException.class);
    }

    @Test
    void createWithEndTimeIsBeforeStartTime() {
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(accountCreatedWithoutStudy));

        assertThatThrownBy(
				() -> studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, endTimeIsBeforeStartTimeCreateDto)
		)
                .isInstanceOf(StudyStartAndEndTimeNotValidException.class);
    }

    @Test
    void updateWithValidateAttribute() {
		given(studyRepository.findById(STUDY_CREATED_ID)).willReturn(Optional.of(createdStudy));
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(managerOfCreatedStudy));

		StudyApiDto.StudyResultDto studyResultDto = studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL, STUDY_CREATED_ID, studyUpdateDto);
        assertThat(studyResultDto.getEmail()).isEqualTo(managerOfCreatedStudy.getEmail());
        assertThat(studyResultDto.getName()).isEqualTo(studyUpdateDto.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(studyUpdateDto.getDescription());
    }

    @Test
    void updateWithNotManagerOfStudy() {
		given(studyRepository.findById(STUDY_CREATED_ID)).willReturn(Optional.of(createdStudy));
		given(accountRepository.findByEmail(ACCOUNT_SETUP_MANAGER_EMAIL))
				.willReturn(Optional.of(managerOfSetUpStudy));

        assertThatThrownBy(
				() -> studyService.updateStudy(ACCOUNT_SETUP_MANAGER_EMAIL, STUDY_CREATED_ID, studyUpdateDto)
		)
                .isInstanceOf(AccountNotManagerOfStudyException.class);
    }

	@Test
	void updateWithStartDateInThePast() {
		given(studyRepository.findById(STUDY_CREATED_ID)).willReturn(Optional.of(createdStudy));
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(managerOfCreatedStudy));

		assertThatThrownBy(
				() -> studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL, STUDY_CREATED_ID, studyStartDateInThePastUpdateDto)
		)
				.isInstanceOf(StudyStartDateInThePastException.class);

		assertThatThrownBy(
				() -> studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL, STUDY_CREATED_ID, studyStartDateTodayUpdateDto)
		)
				.isInstanceOf(StudyStartDateInThePastException.class);
	}

	@Test
	void updateWithEndDateIsBeforeStartDate() {
		given(studyRepository.findById(STUDY_CREATED_ID)).willReturn(Optional.of(createdStudy));
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(accountCreatedWithoutStudy));

		assertThatThrownBy(
				() -> studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL, STUDY_CREATED_ID, endDateIsBeforeStartDateUpdateDto)
		)
				.isInstanceOf(StudyStartAndEndDateNotValidException.class);
	}

	@Test
	void updateWithEndTimeIsBeforeStartTime() {
		given(studyRepository.findById(STUDY_CREATED_ID)).willReturn(Optional.of(createdStudy));
		given(accountRepository.findByEmail(ACCOUNT_CREATED_STUDY_EMAIL))
				.willReturn(Optional.of(accountCreatedWithoutStudy));

		assertThatThrownBy(
				() -> studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL,
						                       STUDY_CREATED_ID,
						                       endTimeIsBeforeStartTimeUpdateDto)
		)
				.isInstanceOf(StudyStartAndEndTimeNotValidException.class);
	}

	@Test
	void updateWithNotExistedId() {
		given(studyRepository.findById(STUDY_NOT_EXISTED_ID)).willReturn(Optional.empty());

		assertThatThrownBy(
				() -> studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL,
						STUDY_NOT_EXISTED_ID,
						studyUpdateDto)
		)
				.isInstanceOf(StudyNotFoundException.class);
	}

    @Test
    void deleteWithExistedId() {
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));
        given(accountRepository.findByEmail(ACCOUNT_SETUP_MANAGER_EMAIL)).willReturn(Optional.of(managerOfSetUpStudy));

		List<Account> listsBeforeDelete = setUpStudy.getAccounts();
		for(Account account : listsBeforeDelete) {
			assertThat(account.getStudy().getId()).isEqualTo(STUDY_SETUP_ID);
		}

        assertThat(managerOfSetUpStudy.getEmail()).isEqualTo(setUpStudy.getEmail());

		StudyApiDto.StudyResultDto studyResultDto = studyService.deleteStudy(ACCOUNT_SETUP_MANAGER_EMAIL, STUDY_SETUP_ID);

        assertThat(studyResultDto.getId()).isEqualTo(setUpStudy.getId());

		List<Account> listsAfterDelete = setUpStudy.getAccounts();
        for(Account account : listsAfterDelete) {
            assertThat(account.getStudy()).isNull();
        }

        verify(studyRepository).delete(setUpStudy);
    }

	@Test
	void deleteWithNotExistedId() {
		given(studyRepository.findById(STUDY_NOT_EXISTED_ID)).willReturn(Optional.empty());

		assertThatThrownBy(
				() -> studyService.deleteStudy(ACCOUNT_CREATED_STUDY_EMAIL, STUDY_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyNotFoundException.class);
	}

    @Test
    void applyWithValidAttribute() {
        given(studyRepository.findByIdForUpdate(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudyForUpdate(STUDY_SETUP_ID);
        int beforeApplyCount = study.getApplyCount();
        studyService.applyStudy(userAccountWithoutStudy, STUDY_SETUP_ID);
        int afterApplyCount = study.getApplyCount();
        assertThat(accountWithoutStudy.getStudy()).isEqualTo(study);

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount - 1);
        assertThat(setUpStudy.getAccounts()).contains(accountWithoutStudy);
        assertThat(accountWithoutStudy.getStudy()).isNotNull();
    }

    @Test
    void applyWithStudyAlreadyExisted() {
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        assertThatThrownBy(() -> studyService.applyStudy(userAccountManagerOfCreatedStudy, STUDY_SETUP_ID))
                .isInstanceOf(StudyAlreadyExistedException.class);
    }

    @Test
    void applyThatSizeIsFull() {
        given(studyRepository.findByIdForUpdate(STUDY_FULL_SIZE_ID)).willReturn(Optional.of(fullSizeStudy));

        assertThatThrownBy(() -> studyService.applyStudy(userAccountWithoutStudy, STUDY_FULL_SIZE_ID))
                .isInstanceOf(StudySizeFullException.class);
    }

	@Test
	void applyNotOpenedStudy() {
		given(studyRepository.findByIdForUpdate(STUDY_CLOSED_ID)).willReturn(Optional.of(closedStudy));

		assertThatThrownBy(
				() -> studyService.applyStudy(userAccountWithoutStudy, STUDY_CLOSED_ID)
		)
				.isInstanceOf(StudyNotInOpenStateException.class);
	}

    @Test
    void cancelWithValidAttribute() {
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(STUDY_SETUP_ID);
        int beforeApplyCount = study.getApplyCount();
		StudyApiDto.StudyApplyResultDto studyApplyResultDto =
				studyService.cancelStudy(userAccountApplierOneOfSetUpStudy, STUDY_SETUP_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount + 1);
        assertThat(setUpStudy.getAccounts()).doesNotContain(applierOfSetUpStudyOne);
        assertThat(applierOfSetUpStudyOne.getStudy()).isNull();
		assertThat(studyApplyResultDto.getId()).isEqualTo(setUpStudy.getId());
    }

	@Test
	void cancelNotOpenedStudy() {
		given(studyRepository.findById(STUDY_CLOSED_ID)).willReturn(Optional.of(closedStudy));

		assertThatThrownBy(
				() -> studyService.cancelStudy(userAccountWithoutStudy, STUDY_CLOSED_ID)
		)
				.isInstanceOf(StudyNotInOpenStateException.class);
	}

	@Test
	void cancelNotAppliedBefore() {
		given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

		assertThatThrownBy(
				() -> studyService.cancelStudy(userAccountManagerOfCreatedStudy, STUDY_SETUP_ID)
		)
				.isInstanceOf(StudyNotAppliedBefore.class);
	}

	@Test
	void getStudyWithExistedId() {
		given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

		Study getStudy = studyService.getStudy(STUDY_SETUP_ID);

		assertThat(getStudy.getId()).isEqualTo(STUDY_SETUP_ID);
	}

	@Test
	void getStudyWithNotExistedId() {
		given(studyRepository.findById(STUDY_NOT_EXISTED_ID)).willReturn(Optional.empty());

		assertThatThrownBy(
				() -> studyService.getStudy(STUDY_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyNotFoundException.class);
	}

//    @Test
//    void listsStudiesWithKeyword() {
//        given(studyRepository.findByBookNameContaining(BOOK_PYTHON_KEYWORD)).willReturn(listBookNamePythonKeywordStudies);
//
//        List<Study> studies = studyService.getStudiesBySearch(BOOK_PYTHON_KEYWORD);
//
//        for(Study study : studies) {
//			assertThat(study.getBookName()).contains(BOOK_PYTHON_KEYWORD);
//		}
//
//		assertThat(setUpStudy.getBookName()).doesNotContain(BOOK_PYTHON_KEYWORD);
//    }

	@Test
	void scheduleOpenToClose() {
		given(studyService.getStudies()).willReturn(listOpenedStudies);

		studyService.scheduleOpenToClose();

		for(Study study : listOpenedStudies) {
			if(study.getStartDate().equals(LocalDate.now())) {
				assertThat(study.getStudyState()).isEqualTo(StudyState.CLOSE);
			} else {
				assertThat(study.getStudyState()).isEqualTo(StudyState.OPEN);
			}
		}
	}

	@Test
	void scheduleCloseToOpen() {
		given(studyService.getStudies()).willReturn(listClosedStudies);

		studyService.scheduleCloseToEnd();

		for(Study study : listClosedStudies) {
			if(study.getEndDate().equals(LocalDate.now())) {
				assertThat(study.getStudyState()).isEqualTo(StudyState.CLOSE);
			} else {
				assertThat(study.getStudyState()).isEqualTo(StudyState.END);
			}
		}
	}
}
