package com.example.bookclub.repository.interview;

import com.example.bookclub.domain.Interview;
import com.example.bookclub.dto.InterviewResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface InterviewRepositoryCustom {
	Page<InterviewResultDto> findAll(Pageable pageable);

	Optional<Interview> findByTitle(String title);
}
