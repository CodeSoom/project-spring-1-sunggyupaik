package com.example.bookclub.repository.study;

import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.dto.QStudyAccountInfoResultDto;
import com.example.bookclub.dto.QStudyInfoResultDto;
import com.example.bookclub.dto.StudyAccountInfoResultDto;
import com.example.bookclub.dto.StudyInfoResultDto;
import com.example.bookclub.errors.StudyNotFoundException;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.example.bookclub.domain.QAccount.account;
import static com.example.bookclub.domain.QStudy.study;

@Repository
public class StudyRepositoryImpl implements StudyRepository {
	private final JPAQueryFactory queryFactory;

	public StudyRepositoryImpl(JPAQueryFactory queryFactory) {
		this.queryFactory = queryFactory;
	}

	@Override
	public StudyInfoResultDto getStudyInfoById(Long id) {
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
}
