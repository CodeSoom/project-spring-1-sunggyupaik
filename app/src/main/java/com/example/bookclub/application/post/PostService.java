package com.example.bookclub.application.post;

import com.example.bookclub.domain.post.Diary;
import com.example.bookclub.domain.post.DiaryRepository;
import com.example.bookclub.domain.post.Post;
import com.example.bookclub.infrastructure.post.ElasticPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 한줄 게시글 저장, 게시글 검색, 게시글 전체 조회를 한다.
 */
@Service
public class PostService {
	private final ElasticPostRepository elasticPostRepository;
	private final DiaryRepository diaryRepository;

	public PostService(ElasticPostRepository elasticPostRepository,
					   DiaryRepository diaryRepository) {
		this.elasticPostRepository = elasticPostRepository;
		this.diaryRepository = diaryRepository;
	}

	/**
	 * 주어진 한줄 게시글을 저장한다
	 *
	 * @param post 한줄 게시글
	 */
	@Transactional
	public void create(Post post) {
		elasticPostRepository.save(post);
	}

	/**
	 * 주어진 한줄 게시글을 저장한다
	 *
	 * @param diary 한줄 게시글
	 */
	@Transactional
	public void createDiary(Diary diary) {
		diaryRepository.save(diary);
	}

	/**
	 * 주어진 한줄 게시글을 저장한다
	 *
	 * @param diary 한줄 게시글
	 * @return 생성된 한줄 게시글
	 */
	@Transactional
	public Diary createDiaryRaw(Diary diary) {
		return diaryRepository.save(diary);
	}

	/**
	 * 주어진 검색어에 해당하는 한줄 게시글 리스트를 반환한다
	 *
	 * @param content 내용
	 * @return 한줄 게시글 리스트
	 */
	@Transactional(readOnly = true)
	public List<Post> findByContent(String content) {
		return elasticPostRepository.findByContent(content);
	}

	/**
	 * 한줄 게시글 리스트를를 반환한다
	 *
	 * @return 한줄 게시글 리스트
	 */
	@Transactional(readOnly = true)
	public List<Post> listAll() {
		return elasticPostRepository.findAll();
	}
}
