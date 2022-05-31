package com.example.bookclub.security.util;

import com.example.bookclub.domain.account.Account;
import org.springframework.stereotype.Component;

/**
 * 주어진 사용자가 스터디 방장인지 여부를 반환한다.
 */
@Component
public class StudyManagerCheck {
	public boolean isManagerOfStudy(Account account) {
		return account.getStudy() != null &&
				account.getStudy().getEmail().equals(account.getEmail());
	}
}
