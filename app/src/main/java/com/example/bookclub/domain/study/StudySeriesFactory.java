package com.example.bookclub.domain.study;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.dto.StudyApiDto;

public interface StudySeriesFactory {
	StudyApiDto.StudyDetailResultDto getDetailedStudy(Account account, Study study);
}
