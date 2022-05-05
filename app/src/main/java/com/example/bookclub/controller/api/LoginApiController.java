package com.example.bookclub.controller.api;

import com.example.bookclub.dto.KakaoLoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginApiController {
	@PostMapping("/kakao-login")
	public String kakaoLogin(@RequestBody KakaoLoginRequest kakaoLoginRequest) {
		return null;
	}
}
