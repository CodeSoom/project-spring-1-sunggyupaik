package com.example.bookclub.infra;

import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaStudyCommentRepository
		extends StudyCommentRepository, CrudRepository<StudyComment, Long> {
	StudyComment save(StudyComment studyComment);

	List<StudyComment> findByStudyId(Long id);
}
