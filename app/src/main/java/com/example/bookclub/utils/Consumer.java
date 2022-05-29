package com.example.bookclub.utils;

import com.example.bookclub.application.PostService;
import com.example.bookclub.domain.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class Consumer {
	private final PostService postService;
	private final ObjectMapper objectMapper;

	public Consumer(PostService postService, ObjectMapper objectMapper) {
		this.postService = postService;
		this.objectMapper = objectMapper;
	}

	@RabbitListener(queues = "CREATE_POST_QUEUE")
	public void handler(String message) throws JsonProcessingException {
		Post post = objectMapper.readValue(message, Post.class);
		post.setCreatedDate(LocalDateTime.now());
		postService.create(post);
	}
}