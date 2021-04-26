package com.example.bookclub.domain;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InterviewRepository {
    Interview save(Interview interview);

    List<Interview> findAll(Pageable pageable);

    List<Interview> findAll();
}
