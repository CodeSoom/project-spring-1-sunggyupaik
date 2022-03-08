package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentLike;
import com.example.bookclub.domain.StudyCommentLikeRepository;
import com.example.bookclub.errors.StudyCommentLikeAlreadyExistedException;
import com.example.bookclub.errors.StudyCommentLikeNotFoundException;
import com.example.bookclub.security.UserAccount;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class StudyCommentLikeService {
	private final StudyCommentLikeRepository studyCommentLikeRepository;
	private final AccountService accountService;
	private final StudyCommentService studyCommentService;

	public StudyCommentLikeService(StudyCommentLikeRepository studyCommentLikeRepository,
								   AccountService accountService,
								   StudyCommentService studyCommentService) {
		this.studyCommentLikeRepository = studyCommentLikeRepository;
		this.accountService = accountService;
		this.studyCommentService = studyCommentService;
	}

	@Transactional
	public Long likeComment(UserAccount userAccount, Long commentId) {
		Long accountId = userAccount.getAccount().getId();

		StudyComment studyComment = studyCommentService.getStudyComment(commentId);
		Account account = accountService.findAccount(accountId);

		StudyCommentLike studyCommentLike = StudyCommentLike.builder()
				.account(account)
				.studyComment(studyComment)
				.build();

		studyCommentLikeRepository.findByStudyCommentAndAccount(studyComment, account)
				.ifPresent(study -> {
					throw new StudyCommentLikeAlreadyExistedException();
				});

		studyCommentLike.addStudyCommentAndAccount(studyComment, account);
		studyCommentLikeRepository.save(studyCommentLike);

		return commentId;
	}

	@Transactional
	public Long unlikeComment(UserAccount userAccount, Long studyCommentId) {
		Long accountId = userAccount.getAccount().getId();

		StudyComment studyComment = studyCommentService.getStudyComment(studyCommentId);
		Account account = accountService.findAccount(accountId);
		StudyCommentLike savedStudyCommentLike = studyCommentLikeRepository.findByStudyCommentAndAccount(studyComment, account)
				.orElseThrow(() -> new StudyCommentLikeNotFoundException(studyCommentId));

		studyCommentLikeRepository.delete(savedStudyCommentLike);

		return savedStudyCommentLike.getId();
	}
}
