package com.example.bookclub.errors.study;

/**
 * 스터디가 모집중이 아닌 경우 예외
 */
public class StudyNotInOpenStateException extends RuntimeException {
	public StudyNotInOpenStateException() {
		super("Study is not in open state");
	}
}
