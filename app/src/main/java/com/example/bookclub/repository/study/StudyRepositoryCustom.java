package com.example.bookclub.repository.study;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.StudyInfoResultDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface StudyRepositoryCustom {
    StudyInfoResultDto getStudyInfo(Long id);

    List<Study> findByBookNameContaining(String keyword, StudyState studyState, Pageable pageable);

    long getStudiesCount(StudyState studyState);

    long getStudiesCountByKeyword(String keyword, StudyState studyState);

    long getAllStudiesCount();

	List<Study> findByFavoriteStudies(List<Long> studyIds);
}
