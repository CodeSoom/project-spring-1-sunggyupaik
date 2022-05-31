package com.example.bookclub.errors.study;

/**
 * 시간 형식이 맞지 않는 경우 예외
 */
public class ParseTimeException extends RuntimeException{
	public ParseTimeException() {
		super("시간 형식이 맞지 않습니다.");
	}
}
