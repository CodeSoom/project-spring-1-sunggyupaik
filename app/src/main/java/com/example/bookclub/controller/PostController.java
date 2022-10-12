package com.example.bookclub.controller;

import com.example.bookclub.application.post.PostService;
import com.example.bookclub.domain.post.Post;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 한줄 게시판 조회 페이지를 요청한다
 */
@Controller
public class PostController {
	private final PostService postService;

	public PostController(PostService postService) {
		this.postService = postService;
	}

	/**
	 * 한줄 게시판 페이지로 이동한다
	 *
	 * @param model 모델
	 * @param content 검색 내용
	 * @return 한줄 게시판 페이지
	 */
	@GetMapping("/posts")
	public String post(Model model,
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
