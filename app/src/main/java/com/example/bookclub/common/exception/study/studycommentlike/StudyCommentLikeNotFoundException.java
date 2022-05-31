package com.example.bookclub.common.exception.study.studycommentlike;

/**
 * 주어진 스터디 댓글 식별자에 해당하는 스터디 댓글 좋아요가 존재하지 않는 경우 예외
 */
public class StudyCommentLikeNotFoundException extends RuntimeException {
	public StudyCommentLikeNotFoundException(Long id) {
		super("StudyCommentLike Not Found: " + id);
	}
}
