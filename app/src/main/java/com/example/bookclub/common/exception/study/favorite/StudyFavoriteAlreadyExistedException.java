package com.example.bookclub.common.exception.study.favorite;

/**
 * 주어진 스터디 즐겨찾기 식별자에 해당하는 스터디 즐겨찾기가 이미 존재하는 경우 예외
 */
public class StudyFavoriteAlreadyExistedException extends RuntimeException{
	public StudyFavoriteAlreadyExistedException(Long id) {
		super("Favorite Already Existed: " + id);
	}
}
