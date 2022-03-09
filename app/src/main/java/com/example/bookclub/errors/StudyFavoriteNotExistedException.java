package com.example.bookclub.errors;

/**
 * 주어진 스터디 줄겨찾기 식별자에 해당하는 스터디 즐겨찾기가 존재하지 않는 경우 예외
 */
public class StudyFavoriteNotExistedException extends RuntimeException{
	public StudyFavoriteNotExistedException(Long id) {
		super("StudyFavorite Not Existed : " + id);
	}
}
