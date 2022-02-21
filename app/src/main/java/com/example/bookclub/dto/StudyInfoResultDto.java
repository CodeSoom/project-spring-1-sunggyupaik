package com.example.bookclub.dto;

import com.example.bookclub.domain.Day;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyInfoResultDto {
	private Long id;

	private String name;

	private String bookName;

	private String contact;

	private LocalDate startDate;

	private LocalDate endDate;

	private String startTime;

	private String endTime;

	@Enumerated(EnumType.STRING)
	private Day day;

	private List<StudyAccountInfoResultDto> studyAccountInfoResultDto;

	@QueryProjection
	public StudyInfoResultDto(Long id, String name, String bookName, String contact, LocalDate startDate,
							  LocalDate endDate, String startTime, String endTime, Day day) {
		this.id = id;
		this.name = name;
		this.bookName = bookName;
		this.contact = contact;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
	}

	public void setStudyAccountInfoResultDto(List<StudyAccountInfoResultDto> studyAccountInfoResultDto) {
		this.studyAccountInfoResultDto = studyAccountInfoResultDto;
	}
}
