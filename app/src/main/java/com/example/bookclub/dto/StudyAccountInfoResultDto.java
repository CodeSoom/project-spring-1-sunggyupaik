package com.example.bookclub.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyAccountInfoResultDto {
	private String name;
	private String email;
	private String nickname;

	@QueryProjection
	public StudyAccountInfoResultDto(String name, String email, String nickname) {
		this.name = name;
		this.email = email;
		this.nickname = nickname;
	}
}
