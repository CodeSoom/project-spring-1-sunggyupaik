package com.example.bookclub.domain;

import com.example.bookclub.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class StudyLike extends BaseTimeEntity {
	@Id
	@GeneratedValue
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	private Study study;

	@ManyToOne(fetch = FetchType.LAZY)
	@ToString.Exclude
	private Account account;

	@Builder
	public StudyLike(Long id, Study study, Account account) {
		this.id = id;
		this.study = study;
		this.account = account;
	}
}
