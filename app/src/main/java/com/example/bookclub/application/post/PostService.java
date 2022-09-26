package com.example.bookclub.application.post;

import com.example.bookclub.domain.post.Diary;
import com.example.bookclub.domain.post.DiaryRepository;
import com.example.bookclub.domain.post.Post;
import com.example.bookclub.infrastructure.post.ElasticPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PostService {
	private final ElasticPostRepository elasticPostRepository;
	private final DiaryRepository diaryRepository;

	public PostService(ElasticPostRepository elasticPostRepository,
					   DiaryRepository diaryRepository) {
		this.elasticPostRepository = elasticPostRepository;
		this.diaryRepository = diaryRepository;
	}

	@Transactional
	public void create(Post post) {
		elasticPostRepository.save(post);
	}

	@Transactional
	public void createDiary(Diary diary) {
		diaryRepository.save(diary);
	}

	@Transactional
	public Diary createDiaryRaw(Diary diary) {
		return diaryRepository.save(diary);
	}

	@Transactional(readOnly = true)
	public List<Post> findByContent(String content) {
		return elasticPostRepository.findByContent(content);
	}

	@Transactional(readOnly = true)
	public List<Post> listAll() {
		return elasticPostRepository.findAll();
	}
}
