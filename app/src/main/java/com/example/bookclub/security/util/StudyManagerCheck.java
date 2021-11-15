package com.example.bookclub.security.util;

import com.example.bookclub.domain.Account;
import org.springframework.stereotype.Component;

@Component
public class StudyManagerCheck {
	public boolean check(Account account) {
		return account.getStudy() != null &&
				account.getStudy().getEmail().equals(account.getEmail());
	}
}
