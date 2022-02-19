package com.example.bookclub.errors;

public class StudyFavoriteNotExistedException extends RuntimeException{
	public StudyFavoriteNotExistedException(Long id) {
		super("StudyFavorite Not Existed : " + id);
	}
}
