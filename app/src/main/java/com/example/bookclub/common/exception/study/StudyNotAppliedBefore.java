package com.example.bookclub.common.exception.study;

/**
 * 스터디 지원을 하지 않은 경우 예외
 */
public class StudyNotAppliedBefore extends RuntimeException {
	public StudyNotAppliedBefore() {
		super("Study not applied before");
	}
}
