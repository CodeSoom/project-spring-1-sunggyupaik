package com.example.bookclub.repository.study.studycommentlike;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import com.example.bookclub.domain.study.studycommentlike.StudyCommentLike;
import com.example.bookclub.domain.study.studycommentlike.StudyCommentLikeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaStudyCommentLikeRepository
		extends StudyCommentLikeRepository, CrudRepository<StudyCommentLike, Long> {
	StudyCommentLike save(StudyCommentLike studyLike);

	Optional<StudyCommentLike> findByStudyCommentAndAccount(StudyComment studyComment, Account account);

	Optional<StudyCommentLike> findById(Long StudyCommentLikeId);

	void delete(StudyCommentLike studyCommentLike);
}
