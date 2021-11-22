package com.example.bookclub.errors;

public class ParseTimeException extends RuntimeException{
	public ParseTimeException() {
		super("시간 형식이 맞지 않습니다.");
	}
}
