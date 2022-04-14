package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyApplyResultDto {
	private Long id;

	@Builder
	public StudyApplyResultDto(Long id) {
		this.id = id;
	}

	public static StudyApplyResultDto of(Long id) {
		return StudyApplyResultDto.builder()
				.id(id)
				.build();
	}
}
