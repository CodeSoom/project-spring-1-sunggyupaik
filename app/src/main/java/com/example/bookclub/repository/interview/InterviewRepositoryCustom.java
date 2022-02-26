package com.example.bookclub.repository.interview;

import com.example.bookclub.dto.InterviewResultDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InterviewRepositoryCustom {
	Page<InterviewResultDto> findAllContainsTileOrContent(String search, Pageable pageable);

	Page<InterviewResultDto> findAll(Pageable pageable);
}
