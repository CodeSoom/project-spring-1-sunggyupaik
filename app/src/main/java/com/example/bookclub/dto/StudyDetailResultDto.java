package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyDetailResultDto {
	private StudyResultDto studyResultDto;

	private List<StudyCommentResultDto> studyComments;

	@Builder
	public StudyDetailResultDto(StudyResultDto studyResultDto, List<StudyCommentResultDto> studyComments) {
		this.studyResultDto = studyResultDto;
		this.studyComments = studyComments;
	}

	public static StudyDetailResultDto of(StudyResultDto studyResultDto,
										  List<StudyCommentResultDto> studyCommentResultDtos) {
		return StudyDetailResultDto.builder()
				.studyResultDto(studyResultDto)
				.studyComments(studyCommentResultDtos)
				.build();
	}
}