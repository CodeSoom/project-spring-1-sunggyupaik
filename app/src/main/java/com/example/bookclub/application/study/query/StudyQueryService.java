package com.example.bookclub.application.study.query;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.StudyApiDto;
import com.example.bookclub.infrastructure.study.JpaStudyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudyQueryService {
	private final JpaStudyRepository studyRepository;

	public StudyQueryService(JpaStudyRepository jpaStudyRepository) {
		this.studyRepository = jpaStudyRepository;
	}

	@Transactional(readOnly = true)
	public List<StudyApiDto.StudyFavoriteDto> getFavoriteStudies(Account account) {
		List<Long> favoriteStudyIds = account.getFavorites()
				.stream().filter(favorite -> favorite.getAccount().getId().equals(account.getId()))
				.map(favorite -> favorite.getStudy().getId())
				.collect(Collectors.toList());

		return studyRepository.findByFavoriteStudies(favoriteStudyIds);
	}
}
