package com.example.bookclub.common.exception.study.studycomment;

/**
 * 스터디 댓글 삭제 요청을 잘못한 경우 예외
 */
public class StudyCommentDeleteBadRequest extends RuntimeException {
	public StudyCommentDeleteBadRequest() {
		super("StudyComment delete bad request");
	}
}
