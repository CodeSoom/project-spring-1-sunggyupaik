package com.example.bookclub.common.exception.study;

/**
 * 스터디가 이미 모집중이거나 진행중인 경우 예외
 */
public class StudyAlreadyInOpenOrCloseException extends RuntimeException {
	public StudyAlreadyInOpenOrCloseException() {
		super("account already has study in open or close");
	}
}
