package com.example.bookclub.common.util;

import com.example.bookclub.application.post.PostService;
import com.example.bookclub.domain.post.Diary;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

public class DiaryConsumer {
	private final PostService postService;
	private final ObjectMapper objectMapper;

	public DiaryConsumer(PostService postService, ObjectMapper objectMapper) {
		this.postService = postService;
		this.objectMapper = objectMapper;
	}

	@RabbitListener(queues = "CREATE_DIARY_QUEUE")
	public void handler(String message) throws JsonProcessingException {
		Diary diary = objectMapper.readValue(message, Diary.class);
		postService.createDiary(diary);
	}
}
