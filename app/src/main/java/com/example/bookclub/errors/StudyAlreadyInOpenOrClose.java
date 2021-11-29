package com.example.bookclub.errors;

public class StudyAlreadyInOpenOrClose extends RuntimeException {
	public StudyAlreadyInOpenOrClose() {
		super("account already has study in open or close");
	}
}
