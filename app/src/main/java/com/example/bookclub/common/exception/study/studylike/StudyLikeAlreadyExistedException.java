package com.example.bookclub.common.exception.study.studylike;

/**
 * 스터디 좋아요가 이미 존재하는 경우 예외
 */
public class StudyLikeAlreadyExistedException extends RuntimeException {
	public StudyLikeAlreadyExistedException() {
		super("StudyLike already existed");
	}
}
