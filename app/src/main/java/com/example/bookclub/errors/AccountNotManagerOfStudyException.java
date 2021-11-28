package com.example.bookclub.errors;

import org.springframework.security.access.AccessDeniedException;

public class AccountNotManagerOfStudyException extends AccessDeniedException {
	public AccountNotManagerOfStudyException(String msg) {
		super(msg);
	}
}
