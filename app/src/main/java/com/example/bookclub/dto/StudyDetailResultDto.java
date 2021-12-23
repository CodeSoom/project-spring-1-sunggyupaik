package com.example.bookclub.dto;

import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyDetailResultDto {
	private Long id;

	private String name;

	private String bookName;

	private String bookImage;

	private String email;

	private String description;

	private String contact;

	private int size;

	private int applyCount;

	private LocalDate startDate;

	private LocalDate endDate;

	private String startTime;

	private String endTime;

	private String day;

	private StudyState studyState;

	private Zone zone;

	private List<StudyCommentResultDto> studyComments;

	@Builder
	public StudyDetailResultDto(Long id, String name, String bookName, String bookImage, String email, String description,
								String contact, int size, int applyCount, LocalDate startDate, LocalDate endDate,
								String startTime, String endTime, String day, StudyState studyState, Zone zone,
								List<StudyCommentResultDto> studyComments) {
		this.id = id;
		this.name = name;
		this.bookName = bookName;
		this.bookImage = bookImage;
		this.email = email;
		this.description = description;
		this.contact = contact;
		this.size = size;
		this.applyCount = applyCount;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
		this.studyState = studyState;
		this.zone = zone;
		this.studyComments = studyComments;
	}
}