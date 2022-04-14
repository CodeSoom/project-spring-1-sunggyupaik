package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyLikesCommentResultDto {
	private Long id;

	@Builder
	public StudyLikesCommentResultDto(Long id) {
		this.id = id;
	}

	public static StudyLikesCommentResultDto of(Long id) {
		return StudyLikesCommentResultDto.builder()
				.id(id)
				.build();
	}
}
