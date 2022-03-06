package com.example.bookclub.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class RememberToken {
	private String series;

	private String username;

	private String tokenValue;

	private Date date;

	@Builder
	public RememberToken(String series, String username, String tokenValue, Date date) {
		this.series = series;
		this.username = username;
		this.tokenValue = tokenValue;
		this.date = date;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setTokenValue(String tokenValue) {
		this.tokenValue = tokenValue;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
