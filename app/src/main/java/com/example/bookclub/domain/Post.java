package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class Post {
	@Id @GeneratedValue
	@Column(name = "POST_ID")
	private Long id;

	private String content;

	@Builder
	public Post(Long id, String content) {
		this.id = id;
		this.content = content;
	}
}
