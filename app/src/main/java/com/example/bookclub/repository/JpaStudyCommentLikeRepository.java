package com.example.bookclub.repository;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentLike;
import com.example.bookclub.domain.StudyCommentLikeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaStudyCommentLikeRepository
		extends StudyCommentLikeRepository, CrudRepository<StudyCommentLike, Long> {
	StudyCommentLike save(StudyCommentLike studyLike);

	Optional<StudyCommentLike> findByStudyCommentAndAccount(StudyComment studyComment, Account account);

	Optional<StudyCommentLike> findById(Long StudyCommentLikeId);

	void delete(StudyCommentLike studyCommentLike);
}
