package com.example.bookclub.domain;

import java.util.List;
import java.util.Optional;

public interface StudyRepository {
    Optional<Study> findById(Long id);

    List<Study> findAll();

    Study save(Study study);

    void delete(Study study);
}
