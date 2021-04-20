package com.example.bookclub.domain;

import java.util.List;
import java.util.Optional;

public interface StudyRepository {
    Optional<Study> findById(Long id);

    Optional<Study> findByEmail(String email);

    List<Study> findAll();

    List<Study> findByStudyState(StudyState studyState);

    List<Study> findByKeyword(String keyword);

    Study save(Study study);

    void delete(Study study);
}
