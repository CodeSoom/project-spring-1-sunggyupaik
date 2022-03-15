package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyLike;
import com.example.bookclub.domain.StudyLikeRepository;
import com.example.bookclub.errors.StudyLikeAlreadyExistedException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class StudyLikeServiceTest {
	private static final Long STUDY_EXISTED_ID = 1L;
	private static final Long STUDY_NOT_EXISTED_ID = 999L;
	private static final Long ACCOUNT_ID = 2L;

	private static final Long STUDY_LIKE_CREATE_ID = 3L;

	private StudyService studyService;
	private AccountService accountService;
	private StudyLikeRepository studyLikeRepository;
	private StudyLikeService studyLikeService;

	private Account account;
	private Study study;
	private UserAccount userAccount;
	private StudyLike createdStudyLike;

	@BeforeEach
	void setUp() {
		studyService = mock(StudyService.class);
		accountService = mock(AccountService.class);
		studyLikeRepository = mock(StudyLikeRepository.class);

		studyLikeService = new StudyLikeService(
			studyService, accountService, studyLikeRepository
		);

		account = Account.builder()
				.id(ACCOUNT_ID)
				.build();

		study = Study.builder()
				.id(STUDY_EXISTED_ID)
				.build();

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));

		userAccount = UserAccount.builder()
				.account(account)
				.authorities(authorities)
				.build();

		createdStudyLike = StudyLike.builder()
				.id(STUDY_LIKE_CREATE_ID)
				.account(account)
				.study(study)
				.build();
	}

	@Test
	void createStudyLikeWithExistedStudyId() {
		given(studyService.getStudy(STUDY_EXISTED_ID)).willReturn(study);
		given(accountService.findAccount(ACCOUNT_ID)).willReturn(account);
		given(studyLikeRepository.save(any(StudyLike.class))).willReturn(createdStudyLike);

		Long createdStudyLikeId = studyLikeService.like(userAccount, STUDY_EXISTED_ID);

		assertThat(createdStudyLikeId).isEqualTo(STUDY_EXISTED_ID);
	}

	@Test
	void createStudyLikeWithNotExistedStudyId() {
		given(studyService.getStudy(STUDY_NOT_EXISTED_ID)).willThrow(StudyNotFoundException.class);
		given(accountService.findAccount(ACCOUNT_ID)).willReturn(account);

		assertThatThrownBy(
				() -> studyLikeService.like(userAccount, STUDY_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyNotFoundException.class);
	}

	@Test
	void createStudyLikeWithAlreadyExistedStudyId() {
		given(studyService.getStudy(STUDY_EXISTED_ID)).willReturn(study);
		given(accountService.findAccount(ACCOUNT_ID)).willReturn(account);
		given(studyLikeRepository.findByStudyAndAccount(study, account))
				.willReturn(Optional.of(createdStudyLike));

		assertThatThrownBy(
				() -> studyLikeService.like(userAccount, STUDY_EXISTED_ID)
		)
				.isInstanceOf(StudyLikeAlreadyExistedException.class);
	}
}
