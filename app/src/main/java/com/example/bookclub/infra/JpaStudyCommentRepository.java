package com.example.bookclub.infra;

import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaStudyCommentRepository
		extends StudyCommentRepository, CrudRepository<StudyComment, Long> {
	StudyComment save(StudyComment studyComment);
}
