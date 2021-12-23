package com.example.bookclub.domain;

import com.example.bookclub.common.BaseEntity;
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
import javax.persistence.Transient;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class StudyComment extends BaseEntity {
	@Id
	@GeneratedValue
	@Column(name ="COMMENT_ID")
	private Long id;

	private String content;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@ToString.Exclude
	private Account account;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STUDY_ID")
	@ToString.Exclude
	private Study study;

	@Transient
	private boolean isWrittenByMe;

	@Builder
	public StudyComment(Long id, String content, Account account, Study study, boolean isWrittenByMe) {
		this.id = id;
		this.content = content;
		this.account = account;
		this.study = study;
		this.isWrittenByMe = isWrittenByMe;
	}
}
