package com.example.bookclub.common.exception.study.studylike;

/**
 * 스터디 좋아요가 존재하지 않는 경우 예외
 */
public class StudyLikeNotExistedException extends RuntimeException{
	public StudyLikeNotExistedException() {
		super("StudyLike not existed");
	}
}
