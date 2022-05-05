package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.KakaoLoginRequest;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class LoginService {
	private AccountAuthenticationService accountAuthenticationService;

	public LoginService(AccountAuthenticationService accountAuthenticationService) {
		this.accountAuthenticationService = accountAuthenticationService;
	}

	public boolean checkAlreadyExistedEmail(KakaoLoginRequest kakaoLoginRequest) {
		String email = kakaoLoginRequest.getEmail();
		return accountAuthenticationService.loadUserByUsername(email) != null;
	}

	public UsernamePasswordAuthenticationToken makeKakaoAuthenticationToken(KakaoLoginRequest kakaoLoginRequest) {
		String email = kakaoLoginRequest.getEmail();
		Account account = accountAuthenticationService.getAccountByEmail(email);

		UsernamePasswordAuthenticationToken accountToken =
				new UsernamePasswordAuthenticationToken(
						new UserAccount(account, List.of(new SimpleGrantedAuthority("KAKAO-USER"))),
						null,
						List.of(new SimpleGrantedAuthority("KAKAO-USER")));

		return accountToken;
	}
}
