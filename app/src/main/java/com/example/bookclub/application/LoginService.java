package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.dto.AccountDto;
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

	/**
	 * 주어진 이메일에 해당하는 사용자가 존재하는지 검사하여 반환한다
	 *
	 * @param kakaoLoginRequest 카카오 로그인에 성공한 이메일 정보
	 * @return 사용자가 기존에 존재하는지 여부
	 */
	public boolean checkAlreadyExistedEmail(AccountDto.KakaoLoginRequest kakaoLoginRequest) {
		String email = kakaoLoginRequest.getEmail();
		return accountRepository.existsByEmail(email);
	}

	/**
	 * 주어진 이메일에 해당하는 사용자 인증토큰을 반환한다
	 *
	 * @param kakaoLoginRequest 카카오 로그인에 성공한 이메일 정보
	 * @return 주어진 이메일에 해당하는 사용자 인증토큰
	 */
	public UsernamePasswordAuthenticationToken makeKakaoAuthenticationToken(
			AccountDto.KakaoLoginRequest kakaoLoginRequest
	) {
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
