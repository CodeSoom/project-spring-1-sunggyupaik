package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class EmailSendResultDto {
	private String email;
	private String authenticationNumber;

	@Builder
	public EmailSendResultDto(String email, String authenticationNumber) {
		this.email = email;
		this.authenticationNumber = authenticationNumber;
	}

	@Builder
	public EmailSendResultDto(String email) {
		this.email = email;
	}


	public static EmailSendResultDto of(String email, String authenticationNumber) {
		return EmailSendResultDto.builder()
				.email(email)
				.authenticationNumber(authenticationNumber)
				.build();
	}
}
