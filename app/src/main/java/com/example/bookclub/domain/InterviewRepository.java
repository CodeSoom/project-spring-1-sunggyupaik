package com.example.bookclub.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface InterviewRepository {
    Interview save(Interview interview);

	Page<Interview> findAll(Pageable pageable);

	List<Interview> findAllByOrderByDateDesc();

	Optional<Interview> findByTitle(String title);
}
