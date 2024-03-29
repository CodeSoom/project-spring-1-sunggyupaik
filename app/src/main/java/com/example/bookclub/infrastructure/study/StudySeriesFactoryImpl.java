package com.example.bookclub.infrastructure.study;

import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.study.Study;
import com.example.bookclub.domain.study.StudySeriesFactory;
import com.example.bookclub.domain.study.studycomment.StudyComment;
import com.example.bookclub.dto.StudyApiDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudySeriesFactoryImpl implements StudySeriesFactory {
	@Override
	public StudyApiDto.StudyDetailResultDto getDetailedStudy(Account account, Study study) {
		Long principalId = account.getId();
		List<StudyComment> studyComments = study.getStudyComments();
		study.addCommentsCount(studyComments.size());
		study.getFavorites().forEach(favorite -> {
			if(favorite.getAccount().getId().equals(principalId))
				study.addFavorite();
		});

		studyComments.forEach(studyComment -> {
			studyComment.setLikesCount(studyComment.getStudyCommentLikes().size());
			studyComment.getStudyCommentLikes().forEach(studyCommentLike -> {
				if(studyCommentLike.getAccount().getId().equals(principalId)) {
					studyComment.addLiked();
				}
			});

			if(studyComment.getAccount().getId().equals(principalId))
				studyComment.setIsWrittenByMeTrue();
		});

		List<StudyApiDto.StudyCommentResultDto> studyCommentResultDtos = studyComments.stream()
				.map(studyComment -> {
					return StudyApiDto.StudyCommentResultDto.of(studyComment, studyComment.getAccount());
				})
				.collect(Collectors.toList());

		return StudyApiDto.StudyDetailResultDto.of(StudyApiDto.StudyResultDto.of(study), studyCommentResultDtos);
	}

	@Override
	public List<StudyApiDto.StudyResultDto> getStudyLists(Account account, List<Study> studies) {
		Long principalId = account.getId();

		return studies.stream()
				.map(study -> {
					study.addLikesCount(study.getStudyLikes().size());
					study.getStudyLikes().forEach(studyLike -> {
						if(studyLike.getAccount().getId().equals(principalId)) {
							study.addLiked();
						}
					});
					return StudyApiDto.StudyResultDto.of(study);
				})
				.collect(Collectors.toList());
	}
}
