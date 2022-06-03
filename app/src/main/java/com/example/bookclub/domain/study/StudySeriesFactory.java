package com.example.bookclub.domain.study;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.StudyApiDto;

import java.util.List;

public interface StudySeriesFactory {
	StudyApiDto.StudyDetailResultDto getDetailedStudy(Account account, Study study);

	List<StudyApiDto.StudyResultDto> getStudyLists(Account account, List<Study> studies);
}
