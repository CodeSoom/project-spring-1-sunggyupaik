package com.example.bookclub.infrastructure.post;

import com.example.bookclub.domain.post.Post;

import java.util.List;

public interface PostRepositoryCustom {
	List<Post> findByContent(String content);
}
