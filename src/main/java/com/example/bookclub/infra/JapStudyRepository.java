package com.example.bookclub.infra;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JapStudyRepository
        extends StudyRepository, CrudRepository<Study, Long> {
    Optional<Study> findById(Long id);

    Study save(Study study);
}
