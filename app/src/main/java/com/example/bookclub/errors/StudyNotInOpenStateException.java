package com.example.bookclub.errors;

public class StudyNotInOpenStateException extends RuntimeException {
	public StudyNotInOpenStateException() {
		super("Study is not in open state");
	}
}
