package com.example.bookclub.repository.interview;

import com.example.bookclub.dto.InterviewDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface InterviewRepositoryCustom {
	Page<InterviewDto.InterviewResultDto> findAllContainsTileOrContent(String search, Pageable pageable);

	Page<InterviewDto.InterviewResultDto> findAll(Pageable pageable);
}
