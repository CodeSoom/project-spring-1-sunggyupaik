package com.example.bookclub.errors;

public class StudyNotAppliedBefore extends RuntimeException {
	public StudyNotAppliedBefore() {
		super("Study not applied before");
	}
}
