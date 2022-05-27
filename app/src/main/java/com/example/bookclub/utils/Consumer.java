package com.example.bookclub.utils;

import com.example.bookclub.domain.Post;
import com.example.bookclub.repository.post.JpaPostRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
	private final JpaPostRepository postRepository;
	private final ObjectMapper objectMapper;

	public Consumer(JpaPostRepository postRepository, ObjectMapper objectMapper) {
		this.postRepository = postRepository;
		this.objectMapper = objectMapper;
	}

	@RabbitListener(queues = "CREATE_POST_QUEUE")
	public void handler(String message) throws JsonProcessingException {
		Post post = objectMapper.readValue(message, Post.class);
		postRepository.save(post);
	}
}
