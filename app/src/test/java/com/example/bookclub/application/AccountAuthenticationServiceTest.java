package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.repository.account.AccountRepositoryCustom;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.UploadFile;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.security.UserAccount;
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

	private final static String ROLE_SETUP_EMAIL = ACCOUNT_SETUP_EMAIL;
	private final static String ROLE_SETUP_NAME = "USER";

	private final static Long STUDY_SETUP_ID = 2L;
	private final static Long UPLOAD_FILE_SETUP_ID = 3L;

	private Account setupAccount;
	private Role setupRole;
	private Study setupStudy;
	private UploadFile setupUploadFile;
	private List<Role> roles;

	private AccountRepositoryCustom accountRepository;
	private RoleRepository roleRepository;
	private AccountAuthenticationService accountAuthenticationService;

	@BeforeEach
	void setup() {
		accountRepository = mock(AccountRepositoryCustom.class);
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

		setupStudy = Study.builder()
				.id(STUDY_SETUP_ID)
				.build();

		setupUploadFile = UploadFile.builder()
				.id(UPLOAD_FILE_SETUP_ID)
				.build();

		setupStudy.addAccount(setupAccount);
		setupAccount.addUploadFile(setupUploadFile);

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

	@Test
	void detailSecurityDetailsWithExistedEmail() {
		given(accountRepository.findByEmail(ACCOUNT_SETUP_EMAIL)).willReturn(Optional.of(setupAccount));
		given(roleRepository.findAllByEmail(ACCOUNT_SETUP_EMAIL)).willReturn(roles);

		UserAccount userAccount = (UserAccount) accountAuthenticationService.loadUserByUsername(ACCOUNT_SETUP_EMAIL);

		assertThat(userAccount.getAccount().getId()).isEqualTo(setupAccount.getId());
		assertThat(userAccount.getAuthorities()).contains(new SimpleGrantedAuthority(roles.get(0).getName()));
		assertThat(userAccount.getAccount().getUploadFile().getId()).isEqualTo(UPLOAD_FILE_SETUP_ID);
		assertThat(userAccount.getAccount().getStudy().getId()).isEqualTo(STUDY_SETUP_ID);
	}

	@Test
	void detailSecurityDetailsWithNotExistedEmail() {
		given(accountRepository.findByEmail(ACCOUNT_NOT_EXISTED_EMAIL)).willReturn(Optional.empty());
		given(roleRepository.findAllByEmail(ACCOUNT_NOT_EXISTED_EMAIL)).willReturn(List.of());

		assertThatThrownBy(
				() -> accountAuthenticationService.loadUserByUsername(ACCOUNT_NOT_EXISTED_EMAIL)
		)
				.isInstanceOf(AccountEmailNotFoundException.class);
	}
}