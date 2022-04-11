package com.example.bookclub.dto;

import com.example.bookclub.domain.Account;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class AccountUpdatePasswordResultDto {
	private Long id;

	private String name;

	private String email;

	private String nickname;

	private String password;

	private boolean deleted;

	@Builder
	public AccountUpdatePasswordResultDto(Long id, String name, String email,
										  String nickname, String password, boolean deleted) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.nickname = nickname;
		this.password = password;
		this.deleted = deleted;
	}

	public static AccountUpdatePasswordResultDto of(Account account) {
		return AccountUpdatePasswordResultDto.builder()
				.id(account.getId())
				.name(account.getName())
				.email(account.getEmail())
				.nickname(account.getNickname())
				.password(account.getPassword())
				.deleted(account.isDeleted())
				.build();
	}
}
