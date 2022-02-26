package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyCommentLikeRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyDetailResultDto;
import com.example.bookclub.dto.StudyInfoResultDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.AccountNotManagerOfStudyException;
import com.example.bookclub.errors.ParseTimeException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyAlreadyInOpenOrClose;
import com.example.bookclub.errors.StudyNotAppliedBefore;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudyNotInOpenStateException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartAndEndDateNotValidException;
import com.example.bookclub.errors.StudyStartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import com.example.bookclub.repository.study.JpaStudyRepository;
import com.example.bookclub.security.UserAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudyService {
    private final JpaStudyRepository studyRepository;
    private final AccountService accountService;
    private final StudyCommentLikeRepository studyCommentLikeRepository;

    public StudyService(JpaStudyRepository studyRepository,
                        AccountService accountService,
                        StudyCommentLikeRepository studyCommentLikeRepository
    ) {
        this.studyRepository = studyRepository;
        this.accountService = accountService;
        this.studyCommentLikeRepository = studyCommentLikeRepository;
    }

    public StudyResultDto createStudy(String email, StudyCreateDto studyCreateDto) {
        Account loginAccount = accountService.findUserByEmail(email);

        if(loginAccount.getStudy() != null) {
            StudyState accountStudyState = loginAccount.getStudy().getStudyState();
            if(loginAccount.getStudy().getStudyState() != null &&
                    (accountStudyState.equals(StudyState.OPEN) || accountStudyState.equals(StudyState.CLOSE))
            ) {
                throw new StudyAlreadyInOpenOrClose();
            }
        }

        if (startDateIsTodayOrBefore(studyCreateDto.getStartDate())) {
            throw new StudyStartDateInThePastException();
        }

        if (startDateIsAfterEndDate(studyCreateDto.getStartDate(),
                studyCreateDto.getEndDate())) {
            throw new StudyStartAndEndDateNotValidException();
        }

        if (startTimeIsAfterEndTime(studyCreateDto.getStartTime(),
                studyCreateDto.getEndTime())) {
            throw new StudyStartAndEndTimeNotValidException();
        }

        Study study = studyCreateDto.toEntity();
        study.addAdmin(loginAccount);
        Study createdStudy = studyRepository.save(study);

        return StudyResultDto.of(createdStudy);
    }

    public StudyResultDto updateStudy(String email, Long id, StudyUpdateDto studyUpdateDto) {
        Study study = getStudy(id);
        Account loginAccount = accountService.findUserByEmail(email);
        if(!study.getEmail().equals(loginAccount.getEmail())) {
            throw new AccountNotManagerOfStudyException();
        }

        if (startDateIsTodayOrBefore(studyUpdateDto.getStartDate())) {
            throw new StudyStartDateInThePastException();
        }

        if (startDateIsAfterEndDate(studyUpdateDto.getStartDate(),
                studyUpdateDto.getEndDate())) {
            throw new StudyStartAndEndDateNotValidException();
        }

        if (startTimeIsAfterEndTime(studyUpdateDto.getStartTime(),
                studyUpdateDto.getEndTime())) {
            throw new StudyStartAndEndTimeNotValidException();
        }

        study.updateWith(studyUpdateDto);
        return StudyResultDto.of(study);
    }

    private boolean startTimeIsAfterEndTime(String startTime, String endTime) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            Date startTimeDate = sdf.parse(startTime);
            Date endTimeDate = sdf.parse(endTime);
            return startTimeDate.after(endTimeDate);
        } catch(ParseException e) {
            throw new ParseTimeException();
        }
    }

    private boolean startDateIsAfterEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }

    private boolean startDateIsTodayOrBefore(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        return startDate.isBefore(today) || startDate.isEqual(today);
    }

    public StudyResultDto deleteStudy(String email, Long id) {
        Study study = getStudy(id);
        Account loginAccount = accountService.findUserByEmail(email);
        if(!study.getEmail().equals(loginAccount.getEmail())) {
            throw new AccountNotManagerOfStudyException();
        }

        study.deleteAccounts();
        studyRepository.delete(study);

        return StudyResultDto.of(study);
    }

    public Long applyStudy(UserAccount userAccount, Long id) {
        Study study = getStudy(id);
        Account account = userAccount.getAccount();

        if(!study.getStudyState().equals(StudyState.OPEN)) {
            throw new StudyNotInOpenStateException();
        }

        if (account.getStudy() != null) {
            throw new StudyAlreadyExistedException();
        }

        if (study.isSizeFull()) {
            throw new StudySizeFullException();
        }

        study.addAccount(account);

        return id;
    }

    public Long cancelStudy(UserAccount userAccount, Long id) {
        Study study = getStudy(id);
        Account account = userAccount.getAccount();

        if(!study.getStudyState().equals(StudyState.OPEN)) {
            throw new StudyNotInOpenStateException();
        }

        if(!study.getAccounts().contains(account)) {
            throw new StudyNotAppliedBefore();
        }

        study.cancelAccount(account);

        return id;
    }

    public List<Study> getStudies() {
        return studyRepository.findAll();
    }

    public Study getStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new StudyNotFoundException(id));
    }

    public StudyDetailResultDto getDetailedStudy(UserAccount userAccount, Long id) {
        Long principalId = userAccount.getAccount().getId();
        Study study = getStudy(id);
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

        List<StudyCommentResultDto> studyCommentResultDtos = studyComments.stream()
                .map(studyComment -> {
                        return StudyCommentResultDto.of(studyComment, studyComment.getAccount());
                    })
                .collect(Collectors.toList());

        return StudyDetailResultDto.of(StudyResultDto.of(study), studyCommentResultDtos);
    }

    public List<StudyResultDto> getStudiesBySearch(String keyword, Long principalId, Pageable pageable) {
        Page<Study> studies = studyRepository.findByBookNameContaining(keyword, pageable);
        if(principalId == null) {
            return studies.stream()
                    .map(StudyResultDto::of)
                    .collect(Collectors.toList());
        }

        studies.forEach(study -> {
            study.addLikesCount(study.getStudyLikes().size());
            study.getStudyLikes().forEach(studyLike -> {
                if(studyLike.getAccount().getId().equals(principalId)) {
                    study.addLiked();
                }
            });
        });

        return studies.stream()
                .map(StudyResultDto::of)
                .collect(Collectors.toList());
    }

    public List<StudyResultDto> getStudiesByStudyState(StudyState studyState, Account account, Pageable pageable) {
        Page<Study> studies = studyRepository.findByStudyState(studyState, pageable);
        if(account == null) {
            return studies.stream()
                    .map(StudyResultDto::of)
                    .collect(Collectors.toList());
        }

        studies.forEach(study -> {
            study.addLikesCount(study.getStudyLikes().size());
            study.getStudyLikes().forEach(like -> {
                if(like.getAccount().getId().equals(account.getId())) {
                    study.addLiked();
                }
            });
        });

        return studies.stream()
                .map(StudyResultDto::of)
                .collect(Collectors.toList());
    }

    public long countAllStudies() {
        return getStudies().size();
    }

    public StudyInfoResultDto getStudyInfo(Long id) {
        System.out.println("getStudyInfo123123");
        return studyRepository.getStudyInfo(id);
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleOpenToClose() {
        List<Study> lists = getStudies().stream()
                .filter(s -> s.getStudyState().equals(StudyState.OPEN))
                .collect(Collectors.toList());

        lists.forEach(study -> {
            LocalDate studyEndDate = study.getStartDate();
            LocalDate nowDate = LocalDate.now();
            if (studyEndDate.isEqual(nowDate)) {
                study.changeOpenToClose();
            }
        });
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleCloseToEnd() {
        List<Study> lists = getStudies().stream()
                .filter(s -> s.getStudyState().equals(StudyState.CLOSE))
                .collect(Collectors.toList());

        lists.forEach(study -> {
            LocalDate studyEndDate = study.getEndDate();
            LocalDate nowDate = LocalDate.now();
            if (nowDate.isAfter(studyEndDate)) {
                study.changeCloseToEnd();
            }
        });
    }

    public long getStudiesCount(StudyState studyState) {
        return studyRepository.getStudiesCount(studyState);
    }

    public long getAllStudiesCount() {
        return studyRepository.getAllStudiesCount();
    }
}
