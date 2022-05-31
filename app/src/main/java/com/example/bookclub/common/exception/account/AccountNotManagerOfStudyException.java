package com.example.bookclub.common.exception.account;

import org.springframework.security.access.AccessDeniedException;

/**
 * 사용자가 스터디 방장이 아닌 경우 에외
 */
public class AccountNotManagerOfStudyException extends AccessDeniedException {
	public AccountNotManagerOfStudyException() {
		super("Not Manager Of Study");
	}
}
