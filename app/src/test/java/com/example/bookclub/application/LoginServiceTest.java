package com.example.bookclub.application;

import com.example.bookclub.application.account.AccountAuthenticationService;
import com.example.bookclub.application.account.LoginService;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.AccountDto;
import com.example.bookclub.common.exception.account.AccountEmailNotFoundException;
import com.example.bookclub.infrastructure.account.JpaAccountRepository;
import com.example.bookclub.security.UserAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
	private AccountDto.KakaoLoginRequest kakaoLoginRequest;
	private AccountDto.KakaoLoginRequest kakaoLoginNotExistedEmailRequest;

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

		kakaoLoginRequest = AccountDto.KakaoLoginRequest.builder()
				.email(ACCOUNT_EXISTED_EMAIL)
				.build();

		kakaoLoginNotExistedEmailRequest = AccountDto.KakaoLoginRequest.builder()
				.email(ACCOUNT_NOT_EXISTED_EMAIL)
				.build();
	}

	@Test
	void checkAlreadyExistedEmailTrue() {
		given(accountRepository.existsByEmail(eq(ACCOUNT_EXISTED_EMAIL)))
				.willReturn(true);

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

	@Test
	void makeKakaoAuthenticationTokenWithNotExistedEmail() {
		given(accountAuthenticationService.getAccountByEmail(ACCOUNT_NOT_EXISTED_EMAIL))
				.willThrow(AccountEmailNotFoundException.class);

 		assertThatThrownBy(
				() -> loginService.makeKakaoAuthenticationToken(kakaoLoginNotExistedEmailRequest)
		)
						.isInstanceOf(AccountEmailNotFoundException.class);
	}
}