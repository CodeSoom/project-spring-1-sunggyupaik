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

	@PostMapping("/kakao-login")
	public EmailSendResultDto kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
		if(loginService.checkAlreadyExistedEmail(kakaoLoginRequest)) {
			UsernamePasswordAuthenticationToken authenticationToken
					= loginService.makeKakaoAuthenticationToken(kakaoLoginRequest);
			System.out.println(authenticationToken+"=authenticationToken");
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);

			return EmailSendResultDto.of(kakaoLoginRequest.getEmail(), null);
		}

		EmailRequestDto emailRequestDto = EmailRequestDto.builder()
				.email(kakaoLoginRequest.getEmail())
				.build();

		System.out.println("업성요 없어요");

		return emailService.saveAuthenticationNumber(emailRequestDto);
	}
}
