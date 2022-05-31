package com.example.bookclub.common.exception.account.emailauthentication;

/**
 * 메세지 생성 요청이 잘못된 경우 예외
 */
public class MessageCreateBadRequestException extends RuntimeException {
	public MessageCreateBadRequestException() {
		super("message create bad request");
	}
}
