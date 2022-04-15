package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyLikeResultDto {
	private Long id;

	@Builder
	public StudyLikeResultDto(Long id) {
		this.id = id;
	}

	public static StudyLikeResultDto of(Long id) {
		return StudyLikeResultDto.builder()
				.id(id)
				.build();
	}
}
