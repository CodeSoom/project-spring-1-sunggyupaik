package com.example.bookclub.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyInfoResultDto {
	private String name;
	private String bookName;
	private String contact;
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;
	private String day;
	private StudyAccountInfoResultDto accountInfoResultDto;

	@QueryProjection
	public StudyInfoResultDto(String name, String bookName, String contact, String startDate,
							  String endDate, String startTime, String endTime, String day) {
		this.name = name;
		this.bookName = bookName;
		this.contact = contact;
		this.startDate = startDate;
		this.endDate = endDate;
		this.startTime = startTime;
		this.endTime = endTime;
		this.day = day;
	}
}
