package com.example.bookclub.repository.study;

import com.example.bookclub.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JpaStudyRepository
        extends StudyRepositoryCustom, JpaRepository<Study, Long> {
    Optional<Study> findById(Long id);

    Optional<Study> findByEmail(String email);

    List<Study> findAll();

    Study save(Study study);

    void delete(Study study);
}
