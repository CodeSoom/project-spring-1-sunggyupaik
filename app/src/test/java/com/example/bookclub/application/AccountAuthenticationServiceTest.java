package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AccountAuthenticationServiceTest {
	private final static Long ACCOUNT_SETUP_ID = 1L;
	private final static String ACCOUNT_SETUP_NAME = "accountName";
	private final static String ACCOUNT_SETUP_EMAIL = "accountEmail";
	private final static String ACCOUNT_SETUP_NICKNAME = "accountNickname";
	private final static String ACCOUNT_SETUP_PASSWORD = "accountPassword";

	private Account setupAccount;

	private AccountRepository accountRepository;
	private RoleRepository roleRepository;
	private AccountAuthenticationService accountAuthenticationService;

	@BeforeEach
	void setup() {
		accountRepository = mock(AccountRepository.class);
		roleRepository = mock(RoleRepository.class);
		accountAuthenticationService = new AccountAuthenticationService(
				roleRepository, accountRepository);

		setupAccount = Account.builder()
				.id(ACCOUNT_SETUP_ID)
				.name(ACCOUNT_SETUP_NAME)
				.email(ACCOUNT_SETUP_EMAIL)
				.nickname(ACCOUNT_SETUP_NICKNAME)
				.password(ACCOUNT_SETUP_PASSWORD)
				.build();
	}

	@Test
	void detailWithExistedEmail() {
		given(accountRepository.findByEmail(ACCOUNT_SETUP_EMAIL)).willReturn(Optional.of(setupAccount));

		Account savedAccount = accountAuthenticationService.getAccountByEmail(ACCOUNT_SETUP_EMAIL);

		assertThat(savedAccount.getId()).isEqualTo(ACCOUNT_SETUP_ID);
		assertThat(savedAccount.getEmail()).isEqualTo(ACCOUNT_SETUP_EMAIL);
	}
}