package com.example.bookclub.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.Id;

@Document(indexName = "post")
@Getter
@NoArgsConstructor
@ToString
public class Post {
	@Id
	private String id;
	private String content;

	public Post(String id, String content) {
		this.id = id;
		this.content = content;
	}
}
