package com.example.bookclub.errors;

public class StudyCommentNotFoundException extends RuntimeException{
	public  StudyCommentNotFoundException(Long id) {
		super("StudyComment not found : " + id);
	}
}
