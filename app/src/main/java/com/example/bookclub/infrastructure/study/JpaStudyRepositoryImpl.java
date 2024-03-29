package com.example.bookclub.infrastructure.study;

import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudyState;
import com.example.bookclub.dto.QStudyApiDto_StudyFavoriteDto;
import com.example.bookclub.dto.QStudyDto_StudyAccountInfoResultDto;
import com.example.bookclub.dto.QStudyDto_StudyInfoResultDto;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.dto.StudyDto;
import com.example.bookclub.common.exception.study.StudyNotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.bookclub.domain.account.QAccount.account;
import static com.example.bookclub.domain.study.QStudy.study;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class JpaStudyRepositoryImpl implements StudyRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public JpaStudyRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public StudyDto.StudyInfoResultDto getStudyInfo(Long id) {
		StudyDto.StudyInfoResultDto studyInfoResultDto = getStudyInfoResultDto(id)
				.orElseThrow(() -> new StudyNotFoundException(id));

		Long findStudyId = studyInfoResultDto.getId();

		List<StudyDto.StudyAccountInfoResultDto> studyAccountInfoResultDto =
				queryFactory
					.select(new QStudyDto_StudyAccountInfoResultDto(account.name, account.email, account.nickname))
					.from(account)
					.where(account.study.id.eq(findStudyId))
					.fetch();

		studyInfoResultDto.setStudyAccountInfoResultDto(studyAccountInfoResultDto);

		return studyInfoResultDto;
	}

	@Override
	public List<Study> findByBookNameContaining(String keyword, StudyState studyState, Pageable pageable) {
		return queryFactory
				.select(study)
				.from(study)
				.where(nameContains(keyword).and(studyStateEq(studyState)))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.orderBy(study.id.desc())
				.fetch();
	}

	private Optional<StudyDto.StudyInfoResultDto> getStudyInfoResultDto(Long id) {
		return Optional.ofNullable(queryFactory
				.select(new QStudyDto_StudyInfoResultDto(
								study.id, study.name, study.bookName, study.contact, study.startDate
								,study.endDate, study.startTime, study.endTime, study.day
						)
				)
				.from(study)
				.where(study.id.eq(id))
				.fetchOne()
		);
	}

	@Override
	public long getStudiesCount(StudyState studyState) {
		return queryFactory
				.selectFrom(study)
				.where(studyStateEq(studyState))
				.fetchCount();
	}

	@Override
	public long getAllStudiesCount() {
		return queryFactory
				.selectFrom(study)
				.fetchCount();
	}

	@Override
	public long getStudiesCountByKeyword(String keyword, StudyState studyState) {
		return queryFactory
				.selectFrom(study)
				.where(nameContains(keyword), studyStateEq(studyState))
				.fetchCount();
	}

	@Override
	public List<StudyApiDto.StudyFavoriteDto> findByFavoriteStudies(List<Long> studyIds) {
		return queryFactory
				.select(new QStudyApiDto_StudyFavoriteDto(
						study.id, study.name, study.bookName, study.studyState
				))
				.from(study)
				.where(studyIdsIn(studyIds))
				.orderBy(study.id.asc())
				.fetch();
	}

	private BooleanBuilder nameContains(String name) {
		return isEmpty(name) ? new BooleanBuilder() : new BooleanBuilder(study.bookName.contains(name));
	}

	private BooleanBuilder studyStateEq(StudyState studyState) {
		return isEmpty(studyState) ? new BooleanBuilder() : new BooleanBuilder(study.studyState.eq(studyState));
	}

	private BooleanBuilder studyIdsIn(List<Long> studyIds) {
		return studyIds.size() == 0 ? new BooleanBuilder(study.id.eq(0L)) : new BooleanBuilder(study.id.in(studyIds));
	}
}
