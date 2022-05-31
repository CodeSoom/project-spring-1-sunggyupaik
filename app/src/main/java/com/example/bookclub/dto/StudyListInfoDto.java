package com.example.bookclub.dto;

import com.example.bookclub.domain.StudyState;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.domain.Page;

@Getter
@Builder
@ToString
public class StudyListInfoDto {
	Page<StudyDto.StudyResultDto> studyResultDtos;
	private StudyState studyState;
	private String search;
	private String searchStudyStateCode;

	public static StudyListInfoDto of(Page<StudyDto.StudyResultDto> studyResultDtos, StudyState studyState, String search) {
		return StudyListInfoDto.builder()
				.studyResultDtos(studyResultDtos)
				.studyState(studyState)
				.search(search)
				.searchStudyStateCode(studyState.getCode().toLowerCase())
				.build();
	}
}
