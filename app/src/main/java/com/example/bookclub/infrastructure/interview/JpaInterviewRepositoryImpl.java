package com.example.bookclub.infrastructure.interview;

import com.example.bookclub.dto.InterviewDto;
import com.example.bookclub.dto.QInterviewDto_InterviewResultDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.example.bookclub.domain.QInterview.interview;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

@Repository
public class JpaInterviewRepositoryImpl implements InterviewRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public JpaInterviewRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public Page<InterviewDto.InterviewResultDto> findAllContainsTileOrContent(String search, Pageable pageable) {
		List<InterviewDto.InterviewResultDto> content = queryFactory
				.select(new QInterviewDto_InterviewResultDto(
						interview.interviewUrl,
						interview.imgUrl,
						interview.author,
						interview.title,
						interview.date,
						interview.content
				))
				.from(interview)
				.where(titleOrContentContains(search))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetch();

		content.forEach(o -> o.setSearch(search));

		long total = queryFactory
				.select(interview)
				.from(interview)
				.where(titleOrContentContains(search))
				.fetchCount();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public Page<InterviewDto.InterviewResultDto> findAll(Pageable pageable) {
		List<InterviewDto.InterviewResultDto> content = queryFactory
				.select(new QInterviewDto_InterviewResultDto(
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

	private BooleanExpression titleOrContentContains(String search) {
		return titleContains(search).or(contentContains(search));
	}

	private BooleanExpression titleContains(String search) {
		return isEmpty(search) ? null : interview.title.contains(search);
	}

	private BooleanExpression contentContains(String search) {
		return isEmpty(search) ? null : interview.content.contains(search);
	}
}
