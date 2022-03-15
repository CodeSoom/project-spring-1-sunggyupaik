package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentRepository;
import com.example.bookclub.dto.StudyCommentCreateDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.errors.StudyCommentNotFoundException;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class StudyCommentServiceTest {
	private static final Long STUDY_COMMENT_EXISTED_ID = 1L;
	private static final String STUDY_COMMENT_CONTENT = "studyCommentContent";
	private static final LocalDateTime STUDY_COMMENT_UPDATED_TIME = LocalDateTime.now();

	private static final Long ACCOUNT_SETUP_ID = 2L;

	private static final Long STUDY_SETUP_ID = 3L;

	private static final Long STUDY_COMMENT_NOT_EXISTED_ID = 999L;

	private Account account;
	private Study study;
	private StudyComment studyComment;

	private UserAccount userAccount;
	private StudyCommentCreateDto studyCommentCreateDto;

	private StudyCommentService studyCommentService;
	private StudyCommentRepository studyCommentRepository;
	private StudyService studyService;

	@BeforeEach
	void setUp() {
		studyCommentRepository = mock(StudyCommentRepository.class);
		studyService = mock(StudyService.class);
		studyCommentService = new StudyCommentService(
				studyCommentRepository, studyService
		);

		account = Account.builder()
				.id(ACCOUNT_SETUP_ID)
				.build();

		study = Study.builder()
				.id(STUDY_SETUP_ID)
				.build();

		studyComment = StudyComment.builder()
				.id(STUDY_COMMENT_EXISTED_ID)
				.content(STUDY_COMMENT_CONTENT)
				.account(account)
				.study(study)
				.build();
		
		studyComment.setUpdatedDate(STUDY_COMMENT_UPDATED_TIME);

		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("USER"));

		userAccount = UserAccount.builder()
				.account(account)
				.authorities(authorities)
				.build();

		studyCommentCreateDto = StudyCommentCreateDto.builder()
				.content(STUDY_COMMENT_CONTENT)
				.build();
	}

	@Test
	void getStudyCommentWithExistedId() {
		given(studyCommentRepository.findById(STUDY_COMMENT_EXISTED_ID)).willReturn(Optional.of(studyComment));

		StudyComment getStudyComment = studyCommentService.getStudyComment(STUDY_COMMENT_EXISTED_ID);

		assertThat(getStudyComment.getId()).isEqualTo(STUDY_COMMENT_EXISTED_ID);
	}

	@Test
	void getStudyCommentWithNotExistedId() {
		given(studyCommentRepository.findById(STUDY_COMMENT_NOT_EXISTED_ID)).willReturn(Optional.empty());

		assertThatThrownBy(
				() -> studyCommentService.getStudyComment(STUDY_COMMENT_EXISTED_ID)
		)
				.isInstanceOf(StudyCommentNotFoundException.class);
	}

	@Test
	void createStudyCommentWithValidAttribute() {
		given(studyService.getStudy(STUDY_SETUP_ID)).willReturn(study);
		given(studyCommentRepository.save(any(StudyComment.class))).willReturn(studyComment);

		StudyCommentResultDto studyCommentResultDto = studyCommentService.createStudyComment(
				userAccount, STUDY_SETUP_ID, studyCommentCreateDto);

		assertThat(studyCommentResultDto.getContent()).isEqualTo(STUDY_COMMENT_CONTENT);
	}
}
