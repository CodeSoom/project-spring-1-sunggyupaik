package com.example.bookclub.infra;

import com.example.bookclub.domain.StudyCommentLike;
import com.example.bookclub.domain.StudyCommentLikeRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaStudyCommentLikeRepository
		extends StudyCommentLikeRepository, CrudRepository<StudyCommentLike, Long> {
	StudyCommentLike save(StudyCommentLike studyLike);
}
