package com.example.bookclub.common;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.account.accounthistory.AccountHistory;
import com.example.bookclub.domain.account.accounthistory.AccountHistoryRepository;
import com.example.bookclub.common.util.BeanUtil;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

/**
 * 계정 수정 히스토리를 저장한다.
 * 수정이 있는경우 자동으로 저장한다.
 */
public class AccountEntityListener {
	@PrePersist
	@PreUpdate
	public void prePersistAndPreUpdate(Object o) {
		AccountHistoryRepository accountHistoryRepository = BeanUtil.getBean(AccountHistoryRepository.class);

		Account account = (Account) o;

		AccountHistory accountHistory = AccountHistory.builder()
				.name(account.getName())
				.email(account.getEmail())
				.nickname(account.getNickname())
				.password(account.getPassword())
				.deleted(account.isDeleted())
				.build();

		accountHistoryRepository.save(accountHistory);
	}
}
