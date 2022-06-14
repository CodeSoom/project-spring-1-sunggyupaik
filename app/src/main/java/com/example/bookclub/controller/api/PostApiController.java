package com.example.bookclub.controller.api;

import com.example.bookclub.application.post.PostService;
import com.example.bookclub.common.response.CommonResponse;
import com.example.bookclub.common.util.Producer;
import com.example.bookclub.domain.post.Diary;
import com.example.bookclub.domain.post.Post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PostApiController {
	private final PostService postService;
	private final Producer producer;
	private final ObjectMapper objectMapper;

	public PostApiController(PostService postService, Producer producer, ObjectMapper objectMapper) {
		this.postService = postService;
		this.producer = producer;
		this.objectMapper = objectMapper;
	}

	@PostMapping("/post")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Post> create(@RequestBody Post post) throws JsonProcessingException {
		String jsonPost = objectMapper.writeValueAsString(post);
		producer.sendTo(jsonPost);
		return CommonResponse.success(post);
	}

	@PostMapping("/diary")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Diary> createDiary(@RequestBody Diary diary) throws JsonProcessingException {
		String jsonPost = objectMapper.writeValueAsString(diary);
		producer.sendTo(jsonPost);
		return CommonResponse.success(diary);
	}
	
	@PostMapping("/diaryRaw")
	@ResponseStatus(HttpStatus.CREATED)
	public CommonResponse<Diary> createDiaryRaw(@RequestBody Diary diary) throws JsonProcessingException {
		Diary response = postService.createDiaryRaw(diary);
		return CommonResponse.success(response);
	}

	@GetMapping("/search")
	public CommonResponse<List<Post>> lists(@RequestParam String content) {
		List<Post> response = postService.findByContent(content);
		return CommonResponse.success(response);
	}
}
