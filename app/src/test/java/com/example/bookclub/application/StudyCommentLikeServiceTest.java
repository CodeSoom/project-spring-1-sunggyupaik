package com.example.bookclub.application;

import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.application.study.StudyCommentLikeService;
import com.example.bookclub.application.study.StudyCommentService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import com.example.bookclub.domain.study.studycommentlike.StudyCommentLike;
import com.example.bookclub.domain.study.studycommentlike.StudyCommentLikeRepository;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.errors.study.studycommentlike.StudyCommentLikeAlreadyExistedException;
import com.example.bookclub.errors.study.studycommentlike.StudyCommentLikeNotFoundException;
import com.example.bookclub.errors.study.studycomment.StudyCommentNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class StudyCommentLikeServiceTest {
	private static final Long STUDY_COMMENT_EXISTED_ID = 1L;
	private static final Long STUDY_COMMENT_NOT_EXISTED_ID = 999L;

	private static final Long STUDY_EXISTED_ID = 2L;
	private static final Long ACCOUNT_EXISTED_ID = 3L;

	private static final Long STUDY_COMMENT_LIKE_CREATE_ID = 4L;

	private Account account;
	private Study study;
	private UserAccount userAccount;
	private StudyComment setUpStudyComment;
	private StudyCommentLike createdStudyCommentLike;

	private StudyCommentLikeService studyCommentLikeService;
	private StudyCommentLikeRepository studyCommentLikeRepository;
	private AccountService accountService;
	private StudyCommentService studyCommentService;

	@BeforeEach
	void setUp() {
		studyCommentLikeRepository = mock(StudyCommentLikeRepository.class);
		accountService = mock(AccountService.class);
		studyCommentService = mock(StudyCommentService.class);
		studyCommentLikeService = new StudyCommentLikeService(
				studyCommentLikeRepository, accountService, studyCommentService
		);

		account = Account.builder()
				.id(ACCOUNT_EXISTED_ID)
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

		setUpStudyComment = StudyComment.builder()
				.account(account)
				.study(study)
				.build();

		createdStudyCommentLike = StudyCommentLike.builder()
				.id(STUDY_COMMENT_LIKE_CREATE_ID)
				.build();
	}

	@Test
	void createLikeCommentWithExistedStudyCommentId() {
		given(studyCommentLikeRepository.save(any(StudyCommentLike.class))).willReturn(createdStudyCommentLike);

		studyCommentLikeRepository.save(createdStudyCommentLike);

		assertThat(createdStudyCommentLike.getId()).isEqualTo(STUDY_COMMENT_LIKE_CREATE_ID);
	}

	@Test
	void createLikeCommentWithNotExistedStudyCommentId() {
		given(studyCommentService.getStudyComment(STUDY_COMMENT_NOT_EXISTED_ID))
				.willThrow(StudyCommentNotFoundException.class);

		assertThatThrownBy(
				() -> studyCommentLikeService.likeComment(userAccount, STUDY_COMMENT_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyCommentNotFoundException.class);
	}

	@Test
	void createLikeCommentWithAlreadyExisted() {
		given(studyCommentService.getStudyComment(STUDY_COMMENT_EXISTED_ID)).willReturn(setUpStudyComment);
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyCommentLikeRepository.findByStudyCommentAndAccount(eq(setUpStudyComment), eq(account)))
				.willReturn(Optional.of(createdStudyCommentLike));

		assertThatThrownBy(
				() -> studyCommentLikeService.likeComment(userAccount, STUDY_COMMENT_EXISTED_ID)
		)
				.isInstanceOf(StudyCommentLikeAlreadyExistedException.class);
	}

	@Test
	void deleteLikeCommentWithExistedCommentId() {
		given(studyCommentService.getStudyComment(STUDY_COMMENT_EXISTED_ID))
				.willReturn(setUpStudyComment);
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyCommentLikeRepository.findByStudyCommentAndAccount(eq(setUpStudyComment), eq(account)))
				.willReturn(Optional.of(createdStudyCommentLike));

		StudyApiDto.StudyLikesCommentResultDto studyLikesCommentResultDto = studyCommentLikeService.unlikeComment(userAccount, STUDY_COMMENT_EXISTED_ID);

		assertThat(studyLikesCommentResultDto.getId()).isEqualTo(STUDY_COMMENT_LIKE_CREATE_ID);
	}

	@Test
	void deleteLikeCommentWithNotExistedCommentId() {
		given(studyCommentService.getStudyComment(STUDY_COMMENT_NOT_EXISTED_ID))
				.willThrow(StudyCommentNotFoundException.class);

		assertThatThrownBy(
				() -> studyCommentLikeService.unlikeComment(userAccount, STUDY_COMMENT_NOT_EXISTED_ID)
		)
				.isInstanceOf(StudyCommentNotFoundException.class);
	}

	@Test
	void deleteLikeCommentWithNotExistedId() {
		given(studyCommentService.getStudyComment(STUDY_COMMENT_EXISTED_ID))
				.willReturn(setUpStudyComment);
		given(accountService.findAccount(ACCOUNT_EXISTED_ID)).willReturn(account);
		given(studyCommentLikeRepository.findByStudyCommentAndAccount(eq(setUpStudyComment), eq(account)))
				.willReturn(Optional.empty());

		assertThatThrownBy(
				() -> studyCommentLikeService.unlikeComment(userAccount, STUDY_COMMENT_EXISTED_ID)
		)
				.isInstanceOf(StudyCommentLikeNotFoundException.class);
	}
}