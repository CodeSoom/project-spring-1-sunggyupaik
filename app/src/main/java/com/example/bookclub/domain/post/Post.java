package com.example.bookclub.domain.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Document(indexName = "post_shard_8_replica_1")
@Getter
@NoArgsConstructor
@ToString
public class Post {
	@Id
	private String id;

	private String content;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Field(type = FieldType.Date, format = DateFormat.custom, pattern = "uuuu-MM-dd HH:mm:ss")
	private LocalDateTime createdDate;

	@Builder
	public Post(String id, String content, LocalDateTime createdDate) {
		this.id = id;
		this.content = content;
		this.createdDate = createdDate;
	}

	public void setCreatedDate(LocalDateTime createdDate) {
		this.createdDate = createdDate;
	}
}
