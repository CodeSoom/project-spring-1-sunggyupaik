package com.example.bookclub.repository.interview;

import com.example.bookclub.domain.Interview;
import com.example.bookclub.dto.InterviewResultDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaInterviewRepositoryImpl implements InterviewRepositoryCustom {
	private final JPAQueryFactory jpaQueryFactory;

	public JpaInterviewRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<InterviewResultDto> findAll(Pageable pageable) {
		return null;
	}

	@Override
	public Optional<Interview> findByTitle(String title) {
		return Optional.empty();
	}
}
