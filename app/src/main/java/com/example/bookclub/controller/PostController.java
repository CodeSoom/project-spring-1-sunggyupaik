package com.example.bookclub.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PostController {
	@GetMapping("/posts")
	public String Post() {
		return "posts/posts";
	}
}
