package com.example.bookclub.controller;

import com.example.bookclub.application.PostService;
import com.example.bookclub.domain.Post;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class PostController {
	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping("/posts")
	public String Post(Model model,
					   @RequestParam(required = false, defaultValue = "") String content
	) {
		List<Post> posts = null;

		if(content.equals("")) {
			posts = postService.listAll();

		} else {
			posts = postService.findByContent(content);
		}
		model.addAttribute("posts", posts);
		model.addAttribute("counts", posts.size());

		return "posts/posts";
	}
}
