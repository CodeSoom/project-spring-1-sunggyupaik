package com.example.bookclub.domain;

import java.util.Optional;

public interface StudyRepository {
    Optional<Study> findById(Long id);

    Study save(Study study);

    void delete(Study study);
}
