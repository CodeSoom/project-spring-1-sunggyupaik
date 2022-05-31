package com.example.bookclub.repository.study.studylike;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.studylike.StudyLike;
import com.example.bookclub.domain.study.studylike.StudyLikeRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaStudyLikeRepository
		extends StudyLikeRepository, CrudRepository<StudyLike, Long> {
	StudyLike save(StudyLike studyLike);

	Optional<StudyLike> findByStudyAndAccount(Study study, Account account);

	void delete(StudyLike studyLike);
}
