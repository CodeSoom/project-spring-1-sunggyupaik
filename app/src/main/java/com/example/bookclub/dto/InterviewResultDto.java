package com.example.bookclub.dto;

import com.example.bookclub.domain.Interview;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class InterviewResultDto {
	private String interviewUrl;

	private String imgUrl;

	private String author;

	private String title;

	private LocalDate date;

	private String content;

	@Builder
	public InterviewResultDto(String interviewUrl, String imgUrl, String author,
							  String title, LocalDate date, String content) {
		this.interviewUrl = interviewUrl;
		this.imgUrl = imgUrl;
		this.author = author;
		this.title = title;
		this.date = date;
		this.content = content;
	}

	public static InterviewResultDto of(Interview interview) {
		return InterviewResultDto.builder()
				.interviewUrl(interview.getInterviewUrl())
				.imgUrl(interview.getImgUrl())
				.author(interview.getAuthor())
				.title(interview.getTitle())
				.date(interview.getDate())
				.content(interview.getContent())
				.build();
	}
}
