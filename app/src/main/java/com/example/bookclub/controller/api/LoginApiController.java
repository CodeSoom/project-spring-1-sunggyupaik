package com.example.bookclub.controller.api;

import com.example.bookclub.application.EmailService;
import com.example.bookclub.application.LoginService;
import com.example.bookclub.dto.EmailRequestDto;
import com.example.bookclub.dto.EmailSendResultDto;
import com.example.bookclub.dto.KakaoLoginRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 카카오 로그인을 요청한다
 */
@RestController
@RequestMapping("/api")
public class LoginApiController {
	private final LoginService loginService;
	private final EmailService emailService;

	public LoginApiController(LoginService loginService,
							  EmailService emailService) {
		this.loginService = loginService;
		this.emailService = emailService;
	}

	/**
	 * 주어진 이메일에 해당하는 사용자가 있으면 해당 사용자를 반환하고 없다면 인증번호를 만들어 반환한다
	 *
	 * @param kakaoLoginRequest 카카오 로그인에 성공한 이메일 정보
	 * @return 생성된 이메일, 인증번호
	 */
	@PostMapping("/kakao-login")
	public EmailSendResultDto kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
		if(loginService.checkAlreadyExistedEmail(kakaoLoginRequest)) {
			UsernamePasswordAuthenticationToken authenticationToken
					= loginService.makeKakaoAuthenticationToken(kakaoLoginRequest);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			return EmailSendResultDto.of(kakaoLoginRequest.getEmail(), null);
		}

		EmailRequestDto emailRequestDto = EmailRequestDto.builder()
				.email(kakaoLoginRequest.getEmail())
				.build();

		return emailService.saveAuthenticationNumber(emailRequestDto);
	}
}
