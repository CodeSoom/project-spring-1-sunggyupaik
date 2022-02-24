package com.example.bookclub.repository.interview;

import com.example.bookclub.domain.Interview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InterviewRepositoryCustom {
	Page<Interview> findAll(Pageable pageable);

	Optional<Interview> findByTitle(String title);
}
