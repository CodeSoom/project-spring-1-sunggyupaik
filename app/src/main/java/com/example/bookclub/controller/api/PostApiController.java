package com.example.bookclub.controller.api;

import com.example.bookclub.domain.Post;
import com.example.bookclub.repository.post.JpaPostRepository;
import com.example.bookclub.utils.Producer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PostApiController {
	private final JpaPostRepository postRepository;
	private final Producer producer;
	private final ObjectMapper objectMapper;

	public PostApiController(JpaPostRepository postRepository, Producer producer, ObjectMapper objectMapper) {
		this.postRepository = postRepository;
		this.producer = producer;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/post")
	public Post create(@RequestBody Post post) throws JsonProcessingException {
		String jsonPost = objectMapper.writeValueAsString(post);
		producer.sendTo(jsonPost);
		return post;
	}
}
