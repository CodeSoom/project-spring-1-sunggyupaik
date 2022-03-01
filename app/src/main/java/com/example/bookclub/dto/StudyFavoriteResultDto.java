package com.example.bookclub.dto;

import com.example.bookclub.domain.StudyState;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StudyFavoriteResultDto {
	private Long id;

	private String name;

	private String bookName;

	private StudyState studyState;

	@QueryProjection
	public StudyFavoriteResultDto(Long id, String name, String bookName, StudyState studyState) {
		this.id = id;
		this.name = name;
		this.bookName = bookName;
		this.studyState = studyState;
	}
}
