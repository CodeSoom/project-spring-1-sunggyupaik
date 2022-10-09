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

	/**
	 * 주어진 스터디 사용자에 해당하는 즐겨찾기 스터디 리스트 정보를 반환한다.
	 * 즐겨찾기 한 스터디 식별자, 이름, 책 이름, 스터디 상태를 포함한다.
	 *
	 * @param account 로그인한 사용자
	 * @return 스터디 사용자에 해당하는 즐겨찾기 스터디 정보
	 */
	@Transactional(readOnly = true)
	public List<StudyApiDto.StudyFavoriteDto> getFavoriteStudies(Account account) {
		List<Long> favoriteStudyIds = account.getFavorites()
				.stream().filter(favorite -> favorite.getAccount().getId().equals(account.getId()))
				.map(favorite -> favorite.getStudy().getId())
				.collect(Collectors.toList());

		return studyRepository.findByFavoriteStudies(favoriteStudyIds);
	}
}
