package com.example.bookclub.errors;

public class StudyFavoriteAlreadyExistedException extends RuntimeException{
	public StudyFavoriteAlreadyExistedException(Long id) {
		super("Favorite Already Existed: " + id);
	}
}
