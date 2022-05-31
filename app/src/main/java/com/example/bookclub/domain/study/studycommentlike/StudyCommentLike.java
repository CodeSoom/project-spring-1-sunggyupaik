package com.example.bookclub.domain.study.studycommentlike;

import com.example.bookclub.common.BaseTimeEntity;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StudyCommentLike extends BaseTimeEntity {
	@Id
	@GeneratedValue
	@Column(name = "STUDYCOMMENTLIKE_ID")
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@ToString.Exclude
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "COMMENT_ID")
	@ToString.Exclude
	private StudyComment studyComment;

	@Builder
	public StudyCommentLike(Long id, Account account, StudyComment studyComment) {
		this.id = id;
		this.account = account;
		this.studyComment = studyComment;
	}

	public void addStudyCommentAndAccount(StudyComment studyComment, Account account) {
		this.studyComment = studyComment;
		this.account = account;
	}
}
