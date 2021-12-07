package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AccountAuthenticationServiceTest {
	private final static Long ACCOUNT_SETUP_ID = 1L;
	private final static String ACCOUNT_SETUP_NAME = "accountName";
	private final static String ACCOUNT_SETUP_EMAIL = "accountEmail";
	private final static String ACCOUNT_SETUP_NICKNAME = "accountNickname";
	private final static String ACCOUNT_SETUP_PASSWORD = "accountPassword";

	private final static String ACCOUNT_NOT_EXISTED_EMAIL = "accountNotExistedEmail";

	private final static Long ROLE_SETUP = 2L;
	private final static String ROLE_SETUP_EMAIL = ACCOUNT_SETUP_EMAIL;
	private final static String ROLE_SETUP_NAME = "USER";

	private Account setupAccount;
	private Role setupRole;
	private List<Role> roles;

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

		setupRole = Role.builder()
				.email(ROLE_SETUP_EMAIL)
				.name(ROLE_SETUP_NAME)
				.build();

		roles = List.of(setupRole);
	}

	@Test
	void detailWithExistedEmail() {
		given(accountRepository.findByEmail(ACCOUNT_SETUP_EMAIL)).willReturn(Optional.of(setupAccount));

		Account savedAccount = accountAuthenticationService.getAccountByEmail(ACCOUNT_SETUP_EMAIL);

		assertThat(savedAccount.getId()).isEqualTo(ACCOUNT_SETUP_ID);
		assertThat(savedAccount.getEmail()).isEqualTo(ACCOUNT_SETUP_EMAIL);
	}

	@Test
	void detailWithNotExistedEmail() {
		given(accountRepository.findByEmail(ACCOUNT_NOT_EXISTED_EMAIL)).willReturn(Optional.empty());

		assertThatThrownBy(
				() -> accountAuthenticationService.getAccountByEmail(ACCOUNT_NOT_EXISTED_EMAIL)
		)
				.isInstanceOf(AccountEmailNotFoundException.class);
	}

	@Test
	void listAllAuthorities() {
		given(roleRepository.findAllByEmail(ACCOUNT_SETUP_EMAIL)).willReturn(roles);

		List<GrantedAuthority> grantedAuthorities = accountAuthenticationService.getAllAuthorities(ACCOUNT_SETUP_EMAIL);

		for(GrantedAuthority grantedAuthority : grantedAuthorities) {
			SimpleGrantedAuthority simpleGrantedAuthority = (SimpleGrantedAuthority) grantedAuthority;
			assertThat(simpleGrantedAuthority.toString()).isEqualTo(ROLE_SETUP_NAME);
		}
	}
}