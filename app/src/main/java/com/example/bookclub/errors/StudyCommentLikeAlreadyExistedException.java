package com.example.bookclub.errors;

public class StudyCommentLikeAlreadyExistedException extends RuntimeException {
	public StudyCommentLikeAlreadyExistedException() {
		super("StudyCommentLike Already Existed");
	}
}
