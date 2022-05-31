package com.example.bookclub.application.study;

import com.example.bookclub.application.account.AccountService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import com.example.bookclub.domain.study.studycommentlike.StudyCommentLike;
import com.example.bookclub.domain.study.studycommentlike.StudyCommentLikeRepository;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.errors.study.studycommentlike.StudyCommentLikeAlreadyExistedException;
import com.example.bookclub.errors.study.studycommentlike.StudyCommentLikeNotFoundException;
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
	 * 주어진 댓글 식별자에 해당하는 스터디 댓글 좋아요를 생성하고 스터디 댓글 식별자를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param commentId 스터디 댓글 식별자
	 * @return 좋아요 누른 스터디 댓글 아이디
	 * @throws StudyCommentLikeAlreadyExistedException 로그인한 사용자의 스터디 댓글 식별자에 이미 좋아요가 존재하는 경우
	 */
	@Transactional
	public StudyApiDto.StudyLikesCommentResultDto likeComment(UserAccount userAccount, Long commentId) {
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

		return StudyApiDto.StudyLikesCommentResultDto.of(commentId);
	}

	/**
	 * 주어진 스터디 댓글 식별자에 해당하는 스터디 댓글 좋아요를 삭제하고 댓글 아이디를 반환한다.
	 *
	 * @param userAccount 로그인한 사용자
	 * @param  commentId 스터디 댓글 식별자
	 * @return 좋아요를 삭제한 스터디 댓글 아이디
	 * @throws StudyCommentLikeNotFoundException 로그인한 사용자의 스터디 댓글 식별자에 좋아요가 존재하지 않는 경우
	 */
	@Transactional
	public StudyApiDto.StudyLikesCommentResultDto unlikeComment(UserAccount userAccount, Long commentId) {
		Long accountId = userAccount.getAccount().getId();

		StudyComment studyComment = studyCommentService.getStudyComment(commentId);
		Account account = accountService.findAccount(accountId);
		StudyCommentLike savedStudyCommentLike = studyCommentLikeRepository.findByStudyCommentAndAccount(studyComment, account)
				.orElseThrow(() -> new StudyCommentLikeNotFoundException(commentId));

		studyCommentLikeRepository.delete(savedStudyCommentLike);

		return StudyApiDto.StudyLikesCommentResultDto.of(savedStudyCommentLike.getId());
	}
}
