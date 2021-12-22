package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCommentResultDto {
	private Long id;
	private String content;
	private Study study;
	private Account account;

	@Builder
	public StudyCommentResultDto(Long id, String content, Study study, Account account) {
		this.id = id;
		this.content = content;
		this.study = study;
		this.account = account;
	}

	public static StudyCommentResultDto of(StudyComment studyComment) {
		return StudyCommentResultDto.builder()
				.id(studyComment.getId())
				.content(studyComment.getContent())
				.study(studyComment.getStudy())
				.account(studyComment.getAccount())
				.build();
	}
}
