package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.KakaoLoginRequest;
import com.example.bookclub.repository.account.JpaAccountRepository;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LoginService {
	private final AccountAuthenticationService accountAuthenticationService;
	private final JpaAccountRepository accountRepository;

	public LoginService(AccountAuthenticationService accountAuthenticationService,
						JpaAccountRepository accountRepository) {
		this.accountAuthenticationService = accountAuthenticationService;
		this.accountRepository = accountRepository;
	}

	public boolean checkAlreadyExistedEmail(KakaoLoginRequest kakaoLoginRequest) {
		String email = kakaoLoginRequest.getEmail();
		return accountRepository.existsByEmail(email);
	}

	public UsernamePasswordAuthenticationToken makeKakaoAuthenticationToken(KakaoLoginRequest kakaoLoginRequest) {
		String email = kakaoLoginRequest.getEmail();
		Account account = accountAuthenticationService.getAccountByEmail(email);

		UsernamePasswordAuthenticationToken accountToken =
				new UsernamePasswordAuthenticationToken(
						new UserAccount(account, List.of(new SimpleGrantedAuthority("KAKAO-USER"))),
						account.getPassword(),
						List.of(new SimpleGrantedAuthority("KAKAO-USER")));

		return accountToken;
	}
}
