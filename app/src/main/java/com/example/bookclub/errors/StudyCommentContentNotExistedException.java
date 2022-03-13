package com.example.bookclub.errors;

public class StudyCommentContentNotExistedException extends RuntimeException {
	public StudyCommentContentNotExistedException() {
		super("StudyComment Not Existed");
	}
}
