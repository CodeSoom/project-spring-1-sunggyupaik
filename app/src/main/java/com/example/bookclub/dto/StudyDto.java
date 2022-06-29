package com.example.bookclub.dto;

import com.example.bookclub.common.EnumMapperValue;
import com.example.bookclub.domain.study.Day;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.domain.study.Zone;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.time.LocalDate;
import java.util.List;

public class StudyDto {
	@Getter
	public static class StudyAccountInfoResultDto {
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

	@Getter
	@Builder
	@ToString
	public static class StudyCreateInfoDto {
		private String bookName;
		private String bookImage;
		List<EnumMapperValue> days;
		List<EnumMapperValue> studyStates;
		List<EnumMapperValue> zones;

		public static StudyCreateInfoDto of(String bookName, String bookImage, List<EnumMapperValue> days,
											List<EnumMapperValue> studyStates, List<EnumMapperValue> zones) {
			return StudyCreateInfoDto.builder()
					.bookName(bookName)
					.bookImage(bookImage)
					.days(days)
					.studyStates(studyStates)
					.zones(zones)
					.build();
		}
	}

	@Getter
	@ToString
	public static class StudyInfoResultDto {
		private Long id;

		private String name;

		private String bookName;

		private String contact;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate startDate;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate endDate;

		private String startTime;

		private String endTime;

		@Enumerated(EnumType.STRING)
		private Day day;

		private List<StudyDto.StudyAccountInfoResultDto> studyAccountInfoResultDto;

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

		public void setStudyAccountInfoResultDto(List<StudyDto.StudyAccountInfoResultDto> studyAccountInfoResultDto) {
			this.studyAccountInfoResultDto = studyAccountInfoResultDto;
		}
	}

	@Getter
	@Builder
	@ToString
	public static class StudyListInfoDto {
		List<StudyApiDto.StudyResultDto> studyResultDtos;
		private StudyState studyState;
		private String search;
		private String searchStudyStateCode;

		public static StudyListInfoDto of(List<StudyApiDto.StudyResultDto> studyResultDtos, StudyState studyState, String search) {
			return StudyListInfoDto.builder()
					.studyResultDtos(studyResultDtos)
					.studyState(studyState)
					.search(search)
					.searchStudyStateCode(studyState.getCode().toLowerCase())
					.build();
		}
	}

	@Getter
	@Builder
	public static class StudyUpdateInfoDto {
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
}
