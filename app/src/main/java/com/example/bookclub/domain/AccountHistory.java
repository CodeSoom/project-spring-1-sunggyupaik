package com.example.bookclub.domain;

import com.example.bookclub.common.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.Hibernate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
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
