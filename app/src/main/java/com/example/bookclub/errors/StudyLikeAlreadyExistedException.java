package com.example.bookclub.errors;

public class StudyLikeAlreadyExistedException extends RuntimeException {
	public StudyLikeAlreadyExistedException() {
		super("StudyLike already existed");
	}
}
