package com.example.bookclub.repository.post;

import com.example.bookclub.domain.Post;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface JpaPostRepository
		extends PostRepositoryCustom, ElasticsearchRepository<Post, String> {
	List<Post> findByContent(String content);
}
