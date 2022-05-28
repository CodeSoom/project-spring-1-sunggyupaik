package com.example.bookclub.repository.post;

import com.example.bookclub.domain.Post;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaPostRepository extends CrudRepository<Post, Long> {
	List<Post> findByContent(String content);
}
