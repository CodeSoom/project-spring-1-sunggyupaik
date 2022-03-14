package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentLike;
import com.example.bookclub.domain.StudyCommentLikeRepository;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
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

	private StudyCommentLikeRepository studyCommentLikeRepository;
	private AccountService accountService;
	private StudyCommentService studyCommentService;

	@BeforeEach
	void setUp() {
		studyCommentLikeRepository = mock(StudyCommentLikeRepository.class);
		accountService = mock(AccountService.class);
		studyCommentService = mock(StudyCommentService.class);

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
	void likeCommentWithExistedId() {
		given(studyCommentLikeRepository.save(any(StudyCommentLike.class))).willReturn(createdStudyCommentLike);

		studyCommentLikeRepository.save(createdStudyCommentLike);

		assertThat(createdStudyCommentLike.getId()).isEqualTo(STUDY_COMMENT_LIKE_CREATE_ID);
	}
}