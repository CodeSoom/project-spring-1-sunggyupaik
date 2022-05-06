package com.example.bookclub.controller.api;

import com.example.bookclub.application.LoginService;
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

	public LoginApiController(LoginService loginService) {
		this.loginService = loginService;
	}

	@PostMapping("/kakao-login")
	public String kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
		if(loginService.checkAlreadyExistedEmail(kakaoLoginRequest)) {
			UsernamePasswordAuthenticationToken authenticationToken
					= loginService.makeKakaoAuthenticationToken(kakaoLoginRequest);
			SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			return "Y";
		}

		return "N";
	}
}
