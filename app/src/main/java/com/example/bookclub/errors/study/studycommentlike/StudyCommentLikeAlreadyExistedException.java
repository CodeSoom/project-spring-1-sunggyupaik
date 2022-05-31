package com.example.bookclub.errors.study.studycommentlike;

/**
 * 스터디 댓글 좋아요가 이미 존재하는 경우 예외
 */
public class StudyCommentLikeAlreadyExistedException extends RuntimeException {
	public StudyCommentLikeAlreadyExistedException() {
		super("StudyCommentLike Already Existed");
	}
}
