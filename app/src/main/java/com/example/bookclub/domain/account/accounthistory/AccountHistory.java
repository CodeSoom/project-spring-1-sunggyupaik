package com.example.bookclub.domain.account.accounthistory;

import com.example.bookclub.common.BaseTimeEntity;
import com.example.bookclub.domain.account.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.Objects;

/**
 * 계정 히스토리
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class AccountHistory extends BaseTimeEntity {
	/* 식별자 */
	@Id @GeneratedValue
	@Column(name = "ACCOUNTHISTORY_ID")
	private Long id;

	/* 이름 */
	private String name;

	/* 이메일 */
	private String email;

	/* 닉네임 */
	private String nickname;

	/* 비밀번호 */
	private String password;

	/* 삭제 여부 */
	private boolean deleted;

	/* 계정 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@ToString.Exclude
	private Account account;

	@Builder
	public AccountHistory(Long id, String name, String email, String nickname,
						  String password, boolean deleted, Account account) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.deleted = deleted;
		this.account = account;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
		AccountHistory that = (AccountHistory) o;
		return Objects.equals(id, that.id);
	}
}
