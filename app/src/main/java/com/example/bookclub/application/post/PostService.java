package com.example.bookclub.application.post;

import com.example.bookclub.domain.post.Diary;
import com.example.bookclub.domain.post.DiaryRepository;
import com.example.bookclub.domain.post.Post;
import com.example.bookclub.infrastructure.post.ElasticPostRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class PostService {
	private final ElasticPostRepository elasticPostRepository;
	private final DiaryRepository diaryRepository;

	public PostService(ElasticPostRepository elasticPostRepository,
					   DiaryRepository diaryRepository) {
		this.elasticPostRepository = elasticPostRepository;
		this.diaryRepository = diaryRepository;
	}

	public void create(Post post) {
		elasticPostRepository.save(post);
	}

	public void createDiary(Diary diary) {
		diaryRepository.save(diary);
	}

	public Diary createDiaryRaw(Diary diary) {
		return diaryRepository.save(diary);
	}

	public List<Post> findByContent(String content) {
		return elasticPostRepository.findByContent(content);
	}

	public List<Post> listAll() {
		return elasticPostRepository.findAll();
	}
}
