package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.KakaoLoginRequest;
import com.example.bookclub.repository.account.JpaAccountRepository;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class LoginServiceTest {
	private final static String ACCOUNT_EXISTED_EMAIL = "accountExistedEmail";
	private final static String ACCOUNT_NOT_EXISTED_EMAIL = "accountNotExistedEmail";

	private LoginService loginService;
	private AccountAuthenticationService accountAuthenticationService;
	private JpaAccountRepository accountRepository;
	private UserAccount userAccount;
	private Account account;
	private UsernamePasswordAuthenticationToken accountToken;
	private KakaoLoginRequest kakaoLoginRequest;
	private KakaoLoginRequest kakaoLoginNotExistedEmailRequest;

	@BeforeEach
	void setUp() {
		accountAuthenticationService = mock(AccountAuthenticationService.class);
		accountRepository = mock(JpaAccountRepository.class);
		loginService = new LoginService(accountAuthenticationService, accountRepository);

		account = Account.builder()
				.email(ACCOUNT_EXISTED_EMAIL)
				.build();

		userAccount = UserAccount.builder()
				.account(account)
				.authorities(List.of(new SimpleGrantedAuthority("USER")))
				.build();

		accountToken = new UsernamePasswordAuthenticationToken(
				new UserAccount(account, List.of(new SimpleGrantedAuthority("KAKAO-USER"))),
				account.getPassword(),
				List.of(new SimpleGrantedAuthority("KAKAO-USER")));

		kakaoLoginRequest = KakaoLoginRequest.builder()
				.email(ACCOUNT_EXISTED_EMAIL)
				.build();

		kakaoLoginNotExistedEmailRequest = KakaoLoginRequest.builder()
				.email(ACCOUNT_NOT_EXISTED_EMAIL)
				.build();
	}

	@Test
	void checkAlreadyExistedEmailTrue() {
		given(accountAuthenticationService.loadUserByUsername(eq(ACCOUNT_EXISTED_EMAIL)))
				.willReturn(userAccount);

		boolean emailExisted = loginService.checkAlreadyExistedEmail(kakaoLoginRequest);

		assertThat(emailExisted).isTrue();
	}

	@Test
	void checkAlreadyExistedEmailFalse() {
		given(accountAuthenticationService.loadUserByUsername(eq(ACCOUNT_NOT_EXISTED_EMAIL)))
				.willReturn(null);

		boolean emailExisted = loginService.checkAlreadyExistedEmail(kakaoLoginNotExistedEmailRequest);

		assertThat(emailExisted).isFalse();
	}

	@Test
	void makeKakaoAuthenticationTokenWithExistedEmail() {
		given(accountAuthenticationService.getAccountByEmail(ACCOUNT_EXISTED_EMAIL)).willReturn(account);

		UsernamePasswordAuthenticationToken kakaoToken = loginService.makeKakaoAuthenticationToken(kakaoLoginRequest);

		assertThat(kakaoToken.getName()).isEqualTo(ACCOUNT_EXISTED_EMAIL);
	}
}