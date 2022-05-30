package com.example.bookclub.dto;

import com.example.bookclub.domain.Interview;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@ToString
public class InterviewResultDto {
	private String interviewUrl;

	private String imgUrl;

	private String author;

	private String title;

	@JsonDeserialize(using = LocalDateDeserializer.class)
	private LocalDate date;

	private String content;

	private String search;

	@Builder
	@QueryProjection
	public InterviewResultDto(String interviewUrl, String imgUrl, String author,
							  String title, LocalDate date, String content, String search) {
		this.interviewUrl = interviewUrl;
		this.imgUrl = imgUrl;
		this.author = author;
		this.title = title;
		this.date = date;
		this.content = content;
		this.search = search;
	}

	@QueryProjection
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

	public static InterviewResultDto of(Interview interview, String search) {
		return InterviewResultDto.builder()
				.interviewUrl(interview.getInterviewUrl())
				.imgUrl(interview.getImgUrl())
				.author(interview.getAuthor())
				.title(interview.getTitle())
				.date(interview.getDate())
				.content(interview.getContent())
				.search(search)
				.build();
	}

	public void setSearch(String search) {
		this.search = search;
	}
}
