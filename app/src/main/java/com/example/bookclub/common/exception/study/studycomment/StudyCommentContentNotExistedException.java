package com.example.bookclub.common.exception.study.studycomment;

public class StudyCommentContentNotExistedException extends RuntimeException {
	public StudyCommentContentNotExistedException() {
		super("StudyComment Not Existed");
	}
}
