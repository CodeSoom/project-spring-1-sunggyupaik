package com.example.bookclub.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

@Getter
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
