package com.example.bookclub.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
@Document(indexName = "post")
public class Post {
	@Id
	private String id;
	private String content;

	@Builder
	public Post(String id) {
		this.id = id;
	}
}
