package com.example.bookclub.errors;

public class StudyCommentDeleteBadRequest extends RuntimeException {
	public StudyCommentDeleteBadRequest() {
		super("StudyComment delete bad request");
	}
}
