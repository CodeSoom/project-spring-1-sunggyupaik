package com.example.bookclub.repository.interview;

import com.example.bookclub.domain.Interview;
import com.example.bookclub.dto.InterviewResultDto;
import com.example.bookclub.dto.QInterviewResultDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.bookclub.domain.QInterview.interview;

@Repository
public class JpaInterviewRepositoryImpl implements InterviewRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public JpaInterviewRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<InterviewResultDto> findAll(Pageable pageable) {
		List<InterviewResultDto> content = queryFactory
				.select(new QInterviewResultDto(
						interview.interviewUrl,
						interview.imgUrl,
						interview.author,
						interview.title,
						interview.date,
						interview.content
				))
				.from(interview)
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		long total = queryFactory
				.select(interview)
				.from(interview)
				.fetchCount();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Optional<Interview> findByTitle(String title) {
		return Optional.empty();
	}
}
