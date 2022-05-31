package com.example.bookclub.domain.study.favorite;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * 즐겨찾기
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@ToString
public class Favorite {
	/* 식별자 */
	@Id
	@GeneratedValue
	private Long id;

	/* 스터디 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STUDY_ID")
	@ToString.Exclude
	private Study study;

	/* 사용자 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@ToString.Exclude
	private Account account;

	@Builder
	public Favorite(Long id, Study study, Account account) {
		this.id = id;
		this.study = study;
		this.account = account;
	}

	/**
	 * 주어진 스터디와 사용자를 즐겨찾기에 추가한다.
	 *
	 * @param study 스터디
	 * @param account 사용자
	 */
	public void addStudyAndAccount(Study study, Account account) {
		this.study = study;
		this.account = account;
	}
}
