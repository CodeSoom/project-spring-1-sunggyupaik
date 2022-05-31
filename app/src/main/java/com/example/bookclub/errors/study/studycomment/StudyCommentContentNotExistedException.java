package com.example.bookclub.errors.study.studycomment;

public class StudyCommentContentNotExistedException extends RuntimeException {
	public StudyCommentContentNotExistedException() {
		super("StudyComment Not Existed");
	}
}
