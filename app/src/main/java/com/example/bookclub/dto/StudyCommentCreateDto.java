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
public class StudyCommentCreateDto {
	private String content;

	@Builder
	public StudyCommentCreateDto(String content) {
		this.content = content;
	}

	public StudyComment toEntity(Account account, Study study) {
		return StudyComment.builder()
				.content(this.content)
				.account(account)
				.study(study)
				.build();
	}
}
