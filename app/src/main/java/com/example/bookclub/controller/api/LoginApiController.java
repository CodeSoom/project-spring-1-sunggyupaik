package com.example.bookclub.controller.api;

import com.example.bookclub.application.LoginService;
import com.example.bookclub.dto.KakaoLoginRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginApiController {
	private LoginService loginService;

	public LoginApiController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping("/kakao-login")
	public String kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
		//1.기존에 이메일이 존재하는 경우
		if(loginService.checkAlreadyExistedEmail(kakaoLoginRequest)) {
			UsernamePasswordAuthenticationToken authenticationToken
					= loginService.makeKakaoAuthenticationToken(kakaoLoginRequest);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			return kakaoLoginRequest.getEmail();
		}

		//2.기존에 이메일이 존재하지 않는 경우
		else {
			return null;
		}
	}
}
