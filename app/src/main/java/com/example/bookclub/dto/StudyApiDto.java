package com.example.bookclub.dto;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Day;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.domain.study.Zone;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class StudyApiDto {
	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyCreateDto {
		private String name;

		private String email;

		private String bookName;

		private String bookImage;

		@Size(min = 10, max = 1000)
		private String description;

		@Size(max = 50)
		private String contact;

		@Min(1)
		@Max(20)
		private int size;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate startDate;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate endDate;

		private String startTime;

		private String endTime;

		private Day day;

		private Zone zone;

		public Study toEntity() {
			return Study.builder()
					.name(this.name)
					.bookName(this.bookName)
					.bookImage(this.bookImage)
					.email(this.email)
					.description(this.description)
					.contact(this.contact)
					.size(this.size)
					.startDate(this.startDate)
					.endDate(this.endDate)
					.startTime(this.startTime)
					.endTime(this.endTime)
					.day(this.day)
					.zone(this.zone)
					.build();
		}

		@Builder
		public StudyCreateDto(String name, String bookName, String bookImage, String email, String description,
							  String contact, int size, LocalDate startDate, LocalDate endDate, String startTime,
							  String endTime, Day day, Zone zone) {
			this.name = name;
			this.bookName = bookName;
			this.bookImage = bookImage;
			this.email = email;
			this.description = description;
			this.contact = contact;
			this.size = size;
			this.startDate = startDate;
			this.endDate = endDate;
			this.startTime = startTime;
			this.endTime = endTime;
			this.day = day;
			this.zone = zone;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class StudyDetailResultDto {
		private StudyApiDto.StudyResultDto studyResultDto;

		private List<StudyCommentResultDto> studyComments;

		@Builder
		public StudyDetailResultDto(StudyApiDto.StudyResultDto studyResultDto,
									List<StudyCommentResultDto> studyComments
		) {
			this.studyResultDto = studyResultDto;
			this.studyComments = studyComments;
		}

		public static StudyDetailResultDto of(StudyResultDto studyResultDto,
											  List<StudyCommentResultDto> studyCommentResultDtos) {
			return StudyDetailResultDto.builder()
					.studyResultDto(studyResultDto)
					.studyComments(studyCommentResultDtos)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyResultDto {
		private Long id;

		private String name;

		private String bookName;

		private String bookImage;

		private String email;

		private String description;

		private String contact;

		private int size;

		private int applyCount;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate startDate;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate endDate;

		private String startTime;

		private String endTime;

		private Day day;

		private StudyState studyState;

		private Zone zone;

		private int likesCount;

		private boolean liked;

		private int commentsCount;

		private boolean isFavorite;

		@Builder
		@QueryProjection
		public StudyResultDto(Long id, String name, String bookName, String bookImage, String email, String description,
							  String contact, int size, int applyCount, LocalDate startDate, LocalDate endDate, String startTime,
							  String endTime, Day day, StudyState studyState, Zone zone, int likesCount, boolean liked,
							  int commentsCount, boolean isFavorite) {
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
			this.likesCount = likesCount;
			this.liked = liked;
			this.commentsCount = commentsCount;
			this.isFavorite = isFavorite;
		}

		@QueryProjection
		public StudyResultDto(Long id, String name, String bookName, String bookImage, String email, String description,
							  String contact, int size, int applyCount, LocalDate startDate, LocalDate endDate,
							  String startTime, String endTime, Day day, StudyState studyState, Zone zone) {
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
		}

		public static StudyResultDto of(Study study) {
			if (study == null) {
				return StudyResultDto.builder().build();
			}

			return StudyResultDto.builder()
					.id(study.getId())
					.name(study.getName())
					.bookName(study.getBookName())
					.bookImage(study.getBookImage())
					.email(study.getEmail())
					.description(study.getDescription())
					.contact(study.getContact())
					.size(study.getSize())
					.applyCount(study.getApplyCount())
					.startDate(study.getStartDate())
					.endDate(study.getEndDate())
					.startTime(study.getStartTime())
					.endTime(study.getEndTime())
					.day(study.getDay())
					.studyState(study.getStudyState())
					.zone(study.getZone())
					.likesCount(study.getStudyLikes() == null ? 0 : study.getStudyLikes().size())
					.liked(study.isLiked())
					.commentsCount(study.getCommentsCount())
					.isFavorite(study.isFavorite())
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	public static class StudyUpdateDto {
		private String name;

		private String description;

		private String contact;

		private int size;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate startDate;

		@JsonDeserialize(using = LocalDateDeserializer.class)
		private LocalDate endDate;

		private String startTime;

		private String endTime;

		private Day day;

		private StudyState studyState;

		private Zone zone;

		@Builder
		public StudyUpdateDto(String name, String description, String contact,
							  int size, LocalDate startDate, LocalDate endDate, String startTime,
							  String endTime, Day day, StudyState studyState, Zone zone) {
			this.name = name;
			this.description = description;
			this.contact = contact;
			this.size = size;
			this.startDate = startDate;
			this.endDate = endDate;
			this.startTime = startTime;
			this.endTime = endTime;
			this.day = day;
			this.studyState = studyState;
			this.zone = zone;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyApplyDto {
		String email;

		@Builder
		public StudyApplyDto(String email) {
			this.email = email;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyApplyResultDto {
		private Long id;

		@Builder
		public StudyApplyResultDto(Long id) {
			this.id = id;
		}

		public static StudyApplyResultDto of(Long id) {
			return StudyApplyResultDto.builder()
					.id(id)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyCommentCreateDto {
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

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyCommentResultDto {
		private Long id;
		private String content;
		private Long studyId;
		private Long accountId;
		private String nickname;
		private boolean isWrittenByMe;
		private String updatedDate;
		private boolean liked;
		private int likesCount;

		@Builder
		public StudyCommentResultDto(Long id, String content, Long studyId, Long accountId, String nickname,
									 boolean isWrittenByMe, String updatedDate, boolean liked, int likesCount) {
			this.id = id;
			this.content = content;
			this.studyId = studyId;
			this.accountId = accountId;
			this.nickname = nickname;
			this.isWrittenByMe = isWrittenByMe;
			this.updatedDate = updatedDate;
			this.liked = liked;
			this.likesCount = likesCount;
		}

		public static StudyCommentResultDto of(StudyComment studyComment, Account account) {
			return StudyCommentResultDto.builder()
					.id(studyComment.getId())
					.content(studyComment.getContent())
					.studyId(studyComment.getStudy().getId())
					.accountId(studyComment.getAccount().getId())
					.nickname(account.getNickname())
					.isWrittenByMe(studyComment.isWrittenByMe())
					.updatedDate(studyComment.getUpdatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
					.liked(studyComment.isLiked())
					.likesCount(studyComment.getLikesCount())
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyFavoriteDto {
		private Long id;

		private String name;

		private String bookName;

		private StudyState studyState;

		@QueryProjection
		public StudyFavoriteDto(Long id, String name, String bookName, StudyState studyState) {
			this.id = id;
			this.name = name;
			this.bookName = bookName;
			this.studyState = studyState;
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyFavoriteResultDto {
		private Long id;

		@Builder
		public StudyFavoriteResultDto(Long id) {
			this.id = id;
		}

		public static StudyFavoriteResultDto of(Long id) {
			return StudyFavoriteResultDto.builder()
					.id(id)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyLikeResultDto {
		private Long id;

		@Builder
		public StudyLikeResultDto(Long id) {
			this.id = id;
		}

		public static StudyLikeResultDto of(Long id) {
			return StudyLikeResultDto.builder()
					.id(id)
					.build();
		}
	}

	@Getter
	@NoArgsConstructor(access = AccessLevel.PROTECTED)
	@ToString
	public static class StudyLikesCommentResultDto {
		private Long id;

		@Builder
		public StudyLikesCommentResultDto(Long id) {
			this.id = id;
		}

		public static StudyLikesCommentResultDto of(Long id) {
			return StudyLikesCommentResultDto.builder()
					.id(id)
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
}
