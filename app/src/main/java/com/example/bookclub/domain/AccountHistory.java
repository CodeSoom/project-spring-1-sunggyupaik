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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountHistory extends BaseTimeEntity {
	@Id
	@GeneratedValue
	private Long id;

	private String name;

	private String email;

	private String nickname;

	private String password;

	private boolean deleted;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ACCOUNT_ID")
	@ToString.Exclude
	private Account account;

	public void setAccount(Account account) {
		this.account = account;
	}

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
}
