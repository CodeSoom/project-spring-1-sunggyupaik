package com.example.bookclub.application;

import com.amazonaws.services.s3.AmazonS3;
import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.AccountNotManagerOfStudyException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyAlreadyInOpenOrClose;
import com.example.bookclub.errors.StudyNotAppliedBefore;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartAndEndDateNotValidException;
import com.example.bookclub.errors.StudyStartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
    private static final int STUDY_SETUP_APPLY_COUNT = 3;
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

    private static final String PYTHON_KEYWORD = "파이썬";
    private static final String PYTHON_BOOKNAME_ONE = "러닝 파이썬 - 상편";
    private static final String PYTHON_BOOKNAME_TWO = "파이썬 웹 프로그래밍";

    private static final LocalDate STUDY_PAST_START_DATE = LocalDate.now().minusDays(3);
    private static final LocalDate STUDY_TODAY_START_DATE = LocalDate.now();
    private static final LocalDate STUDY_LATE_START_DATE = LocalDate.now().plusDays(5);
    private static final LocalDate STUDY_EARLY_END_DATE = LocalDate.now().plusDays(3);

    private static final String STUDY_LATE_START_TIME = "15:00";
    private static final String STUDY_EARLY_END_TIME = "13:00";

    private static final Long STUDY_NOT_EXISTED_ID = 100L;
    private static final Long STUDY_FULL_SIZE_ID = 8L;


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
    private Study openedStudyOne;
    private Study openedStudyTwo;
    private Study closedStudyOne;
    private Study closedStudyTwo;
    private Study endedStudyOne;
    private Study endedStudyTwo;
    private Study pythonBookNameStudyOne;
    private Study pythonBookNameStudyTwo;

    private StudyCreateDto studyCreateDto;
    private StudyCreateDto studyStartDateInThePastCreateDto;
    private StudyCreateDto studyStartDateTodayCreateDto;
    private StudyCreateDto endDateIsBeforeStartDateCreateDto;
    private StudyCreateDto endTimeIsBeforeStartTimeCreateDto;
    private StudyUpdateDto studyUpdateDto;
	private StudyUpdateDto studyStartDateInThePastUpdateDto;
	private StudyUpdateDto studyStartDateTodayUpdateDto;
	private StudyUpdateDto endDateIsBeforeStartDateUpdateDto;
	private StudyUpdateDto endTimeIsBeforeStartTimeUpdateDto;

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
	private EmailAuthenticationRepository emailAuthenticationRepository;
	private RoleRepository roleRepository;
	private AmazonS3 amazonS3;
	private UploadFileService uploadFileService;

	private AccountService accountService;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        accountRepository = mock(AccountRepository.class);
		emailAuthenticationRepository = mock(EmailAuthenticationRepository.class);
		roleRepository = mock(RoleRepository.class);
		amazonS3 = mock(AmazonS3.class);
		uploadFileService = new UploadFileService(amazonS3);
		passwordEncoder = new BCryptPasswordEncoder();
		accountService = new AccountService(accountRepository, emailAuthenticationRepository,
				                            passwordEncoder, uploadFileService, roleRepository);
        studyService = new StudyService(studyRepository, accountService);

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

        studyStartDateInThePastCreateDto = StudyCreateDto.builder()
                .startDate(STUDY_PAST_START_DATE)
                .build();

        studyStartDateTodayCreateDto = StudyCreateDto.builder()
                .startDate(STUDY_TODAY_START_DATE)
                .build();

        endDateIsBeforeStartDateCreateDto = StudyCreateDto.builder()
                .startDate(STUDY_LATE_START_DATE)
                .endDate(STUDY_EARLY_END_DATE)
                .build();

        endTimeIsBeforeStartTimeCreateDto = StudyCreateDto.builder()
                .startDate(STUDY_CREATED_START_DATE)
                .endDate(STUDY_CREATED_END_DATE)
                .startTime(STUDY_LATE_START_TIME)
                .endTime(STUDY_EARLY_END_TIME)
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

		studyStartDateInThePastUpdateDto = StudyUpdateDto.builder()
				.startDate(STUDY_PAST_START_DATE)
				.build();

		studyStartDateTodayUpdateDto = StudyUpdateDto.builder()
				.startDate(STUDY_TODAY_START_DATE)
				.build();

		endDateIsBeforeStartDateUpdateDto = StudyUpdateDto.builder()
				.startDate(STUDY_LATE_START_DATE)
				.endDate(STUDY_EARLY_END_DATE)
				.build();

		endTimeIsBeforeStartTimeUpdateDto = StudyUpdateDto.builder()
				.startDate(STUDY_CREATED_START_DATE)
				.endDate(STUDY_CREATED_END_DATE)
				.startTime(STUDY_LATE_START_TIME)
				.endTime(STUDY_EARLY_END_TIME)
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

        StudyResultDto studyResultDto = studyService.createStudy(ACCOUNT_CREATED_STUDY_EMAIL, studyCreateDto);

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
                .isInstanceOf(StudyAlreadyInOpenOrClose.class);
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

        StudyResultDto studyResultDto = studyService.updateStudy(ACCOUNT_CREATED_STUDY_EMAIL, STUDY_CREATED_ID, studyUpdateDto);
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

        StudyResultDto studyResultDto = studyService.deleteStudy(ACCOUNT_SETUP_MANAGER_EMAIL, STUDY_SETUP_ID);

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
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(STUDY_SETUP_ID);
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
        given(studyRepository.findById(STUDY_FULL_SIZE_ID)).willReturn(Optional.of(fullSizeStudy));

        assertThatThrownBy(() -> studyService.applyStudy(userAccountWithoutStudy, STUDY_FULL_SIZE_ID))
                .isInstanceOf(StudySizeFullException.class);
    }

    @Test
    void cancelWithValidAttribute() {
        given(studyRepository.findById(STUDY_SETUP_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(STUDY_SETUP_ID);
        int beforeApplyCount = study.getApplyCount();
        Long canceledStudyId = studyService.cancelStudy(userAccountApplierOneOfSetUpStudy, STUDY_SETUP_ID);
        int afterApplyCount = study.getApplyCount();

        assertThat(beforeApplyCount).isEqualTo(afterApplyCount + 1);
        assertThat(setUpStudy.getAccounts()).doesNotContain(applierOfSetUpStudyOne);
        assertThat(applierOfSetUpStudyOne.getStudy()).isNull();
		assertThat(canceledStudyId).isEqualTo(setUpStudy.getId());
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
