package com.example.bookclub.dto;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.EnumMapperValue;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class StudyUpdateInfoDto {
	private Long id;

	private String name;

	private String bookName;

	private String bookImage;

	private String description;

	private String contact;

	private int size;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate startDate;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate endDate;

	private String startTime;

	private String endTime;

	private List<EnumMapperValue> days;

	private List<EnumMapperValue> studyStates;

	private List<EnumMapperValue> zones;

	public static StudyUpdateInfoDto of (Study study) {
		return StudyUpdateInfoDto.builder()
				.id(study.getId())
				.name(study.getName())
				.bookName(study.getBookName())
				.bookImage(study.getBookImage())
				.description(study.getDescription())
				.contact(study.getContact())
				.size(study.getSize())
				.startDate(study.getStartDate())
				.endDate(study.getEndDate())
				.startTime(study.getStartTime())
				.endTime(study.getEndTime())
				.days(Day.getAllDaysSelectedWith(study.getDay()))
				.studyStates(StudyState.getAllStudyStatesSelectedWith(study.getStudyState()))
				.zones(Zone.getAllZonesSelectedWith(study.getZone()))
				.build();

	}
}
