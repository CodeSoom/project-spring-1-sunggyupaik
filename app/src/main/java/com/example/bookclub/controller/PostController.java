package com.example.bookclub.controller;

import com.example.bookclub.application.PostService;
import com.example.bookclub.domain.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class PostController {
	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	@GetMapping("/posts")
	public String Post(Model model) {
		List<Post> posts = postService.listAll();
		model.addAttribute("posts", posts);
		model.addAttribute("counts", posts.size());

		return "posts/posts";
	}
}
