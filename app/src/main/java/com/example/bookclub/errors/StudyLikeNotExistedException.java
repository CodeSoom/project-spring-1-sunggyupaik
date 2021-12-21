package com.example.bookclub.errors;

public class StudyLikeNotExistedException extends RuntimeException{
	public StudyLikeNotExistedException() {
		super("StudyLike not existed");
	}
}
