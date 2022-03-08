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

/**
 * 스터디 댓글에 좋아요 신청, 취소를 한다.
 */
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

	/**
	 * 로그인한 사용자와 댓글 아이디로 스터디 좋아요를 생성하고 댓글 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param commentId 댓글 아이디 식별자
	 * @return 좋아요 누른 스터디 댓글 아이디
	 * @throws StudyCommentLikeAlreadyExistedException 댓글을 이미 좋아요를 생성한 경우
	 */
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

	/**
	 * 로그인한 사용자와 댓글 아이디로 스터디 좋아요를 추가하고 댓글 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param  commentId 아이디 식별자
	 * @return 좋아요 누른 스터디 댓글 아이디
	 * @throws StudyCommentLikeNotFoundException 댓글을 이미 좋아요를 삭제한 경우
	 */
	@Transactional
	public Long unlikeComment(UserAccount userAccount, Long commentId) {
		Long accountId = userAccount.getAccount().getId();

		StudyComment studyComment = studyCommentService.getStudyComment(commentId);
		Account account = accountService.findAccount(accountId);
		StudyCommentLike savedStudyCommentLike = studyCommentLikeRepository.findByStudyCommentAndAccount(studyComment, account)
				.orElseThrow(() -> new StudyCommentLikeNotFoundException(commentId));

		studyCommentLikeRepository.delete(savedStudyCommentLike);

		return savedStudyCommentLike.getId();
	}
}
