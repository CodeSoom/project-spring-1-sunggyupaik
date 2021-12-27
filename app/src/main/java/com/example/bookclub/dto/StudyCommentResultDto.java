package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.StudyComment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudyCommentResultDto {
	private Long id;
	private String content;
	private Long studyId;
	private Long accountId;
	private String nickname;
	private UploadFileResultDto uploadFileResultDto;
	private boolean isWrittenByMe;
	private String updatedDate;

	@Builder
	public StudyCommentResultDto(Long id, String content, Long studyId, Long accountId, String nickname,
								 UploadFileResultDto uploadFileResultDto, boolean isWrittenByMe, String updatedDate) {
		this.id = id;
		this.content = content;
		this.studyId = studyId;
		this.accountId = accountId;
		this.nickname = nickname;
		this.uploadFileResultDto = uploadFileResultDto;
		this.isWrittenByMe = isWrittenByMe;
		this.updatedDate = updatedDate;
	}

	public static StudyCommentResultDto of(StudyComment studyComment, Account account) {
		return StudyCommentResultDto.builder()
				.id(studyComment.getId())
				.content(studyComment.getContent())
				.studyId(studyComment.getStudy().getId())
				.accountId(studyComment.getAccount().getId())
				.nickname(account.getNickname())
				.uploadFileResultDto(
						account.getUploadFile() == null ? null : UploadFileResultDto.of(account.getUploadFile())
				)
				.isWrittenByMe(studyComment.isWrittenByMe())
				.updatedDate(studyComment.getUpdatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
				.build();
	}
}
