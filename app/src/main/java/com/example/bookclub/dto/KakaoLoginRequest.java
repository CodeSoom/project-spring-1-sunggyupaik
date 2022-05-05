package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KakaoLoginRequest {
	private String email;

	@Builder
	public KakaoLoginRequest(String email) {
		this.email = email;
	}

	public static KakaoLoginRequest of(String email) {
		return KakaoLoginRequest.builder()
				.email(email)
				.build();
	}
}
