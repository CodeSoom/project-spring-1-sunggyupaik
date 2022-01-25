package com.example.bookclub.errors;

public class StudyCommentLikeNotFoundException extends RuntimeException {
	public StudyCommentLikeNotFoundException(Long id) {
		super("StudyCommentLike Not Found: " + id);
	}
}
