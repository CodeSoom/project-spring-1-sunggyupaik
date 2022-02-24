package com.example.bookclub.domain;

import com.example.bookclub.application.AccountAuthenticationService;
import com.example.bookclub.infra.JpaAccountHistoryRepository;
import com.example.bookclub.infra.JpaRoleRepository;
import com.example.bookclub.infra.account.AccountRepositoryCustom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AccountTest {
	@Autowired
	private AccountRepositoryCustom accountRepository;

	@Autowired
	private JpaRoleRepository jpaRoleRepository;

	@Autowired
	private AccountAuthenticationService accountAuthenticationService;

	@Autowired
	private AccountHistoryRepository accountHistoryRepository;

	@Autowired
	private JpaAccountHistoryRepository jpaAccountHistoryRepository;

	@Test
	void saveAccount() {
		Account account = Account.builder()
				.name("hello")
				.nickname("babo")
				.build();

		Account savedAccount = accountRepository.save(account);

		System.out.println(accountHistoryRepository.findById(1L));
	}

	@Test
	void testAccountHistory() {
		AccountHistory accountHistory = AccountHistory.builder()
				.id(1L)
				.name("hello")
				.nickname("babo")
				.build();

		accountHistoryRepository.save(accountHistory);
	}
}