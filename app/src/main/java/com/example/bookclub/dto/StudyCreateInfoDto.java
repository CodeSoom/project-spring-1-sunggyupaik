package com.example.bookclub.dto;

import com.example.bookclub.domain.EnumMapperValue;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@Builder
@ToString
public class StudyCreateInfoDto {
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
