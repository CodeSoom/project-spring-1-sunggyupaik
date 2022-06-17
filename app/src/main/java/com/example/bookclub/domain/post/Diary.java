package com.example.bookclub.domain.post;


import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(callSuper = true)
public class Diary {
	@Id @GeneratedValue
	private Long id;

	private String content;

	@Builder
	public Diary(Long id, String content) {
		this.id = id;
		this.content = content;
	}
}
