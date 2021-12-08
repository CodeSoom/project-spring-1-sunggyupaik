package com.example.bookclub.errors;

public class StudyAlreadyInOpenException extends RuntimeException {
	public StudyAlreadyInOpenException() {
		super("Study already started");
	}
}
