package com.example.bookclub.errors;

public class MessageCreateBadRequestException extends RuntimeException {
	public MessageCreateBadRequestException() {
		super("message create bad request");
	}
}
