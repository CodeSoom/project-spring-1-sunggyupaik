package com.example.bookclub.repository.study;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.QStudyAccountInfoResultDto;
import com.example.bookclub.dto.QStudyFavoriteResultDto;
import com.example.bookclub.dto.QStudyInfoResultDto;
import com.example.bookclub.dto.StudyAccountInfoResultDto;
import com.example.bookclub.dto.StudyFavoriteResultDto;
import com.example.bookclub.dto.StudyInfoResultDto;
import com.example.bookclub.errors.StudyNotFoundException;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.example.bookclub.domain.QAccount.account;
import static com.example.bookclub.domain.QStudy.study;
import static org.apache.commons.lang3.ObjectUtils.isEmpty;

public class JpaStudyRepositoryImpl implements StudyRepositoryCustom {
	private final JPAQueryFactory queryFactory;

	public JpaStudyRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public StudyInfoResultDto getStudyInfo(Long id) {
		StudyInfoResultDto studyInfoResultDto = getStudyInfoResultDto(id)
				.orElseThrow(() -> new StudyNotFoundException(id));

		Long findStudyId = studyInfoResultDto.getId();

		List<StudyAccountInfoResultDto> studyAccountInfoResultDto =
				queryFactory
					.select(new QStudyAccountInfoResultDto(account.name, account.email, account.nickname))
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

	private Optional<StudyInfoResultDto> getStudyInfoResultDto(Long id) {
		return Optional.ofNullable(queryFactory
				.select(new QStudyInfoResultDto(
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
	public List<StudyFavoriteResultDto> findByFavoriteStudies(List<Long> studyIds) {
		return queryFactory
				.select(new QStudyFavoriteResultDto(
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
