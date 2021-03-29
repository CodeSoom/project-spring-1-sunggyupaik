package com.example.bookclub.infra;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JapStudyRepository
        extends StudyRepository, CrudRepository<Study, Long> {
    Optional<Study> findById(Long id);

    List<Study> findAll();

    Study save(Study study);

    void delete(Study study);
}
