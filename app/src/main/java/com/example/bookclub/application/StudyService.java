package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyComment;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.StudyApplyResultDto;
import com.example.bookclub.dto.StudyCommentResultDto;
import com.example.bookclub.dto.StudyDto;
import com.example.bookclub.dto.StudyInfoResultDto;
import com.example.bookclub.errors.AccountNotManagerOfStudyException;
import com.example.bookclub.errors.ParseTimeException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyAlreadyInOpenOrCloseException;
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
import org.springframework.data.domain.PageImpl;
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

/**
 * 스터디 생성, 수정, 조회, 지원 생성, 지원 삭제, 검색, 스터디상태 변경, 갯수 조회를 한다.
 */
@Service
@Transactional
public class StudyService {
    private final JpaStudyRepository studyRepository;
    private final AccountService accountService;

    public StudyService(JpaStudyRepository studyRepository,
                        AccountService accountService
    ) {
        this.studyRepository = studyRepository;
        this.accountService = accountService;
    }

    /**
     * 주어진 사용자 식별자과 생성할 스터디 정보로 스터디를 생성하고 반환한다.
     *
     * @param email 사용자 식별자
     * @param studyCreateDto 생성할 스터디 정보
     * @return 생성된 스터디 정보
     * @throws StudyAlreadyInOpenOrCloseException 이미 모집중이거나 진행중 스터디에 참여하고 있는 경우
     * @throws StudyStartDateInThePastException 생성하려는 스터디 시작일이 과거인 경우
     * @throws StudyStartAndEndDateNotValidException 생성하려는 스터디 종료일이 시작일보다 빠른 경우
     * @throws StudyStartAndEndTimeNotValidException 생성하려는 스터디 종료시간이 시작시간보다 빠른 경우
     */
    public StudyDto.StudyResultDto createStudy(String email, StudyDto.StudyCreateDto studyCreateDto) {
        Account loginAccount = accountService.findAccountByEmail(email);

        if(loginAccount.getStudy() != null) {
            StudyState accountStudyState = loginAccount.getStudy().getStudyState();
            if(loginAccount.getStudy().getStudyState() != null &&
                    (accountStudyState.equals(StudyState.OPEN) || accountStudyState.equals(StudyState.CLOSE))
            ) {
                throw new StudyAlreadyInOpenOrCloseException();
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

        return StudyDto.StudyResultDto.of(createdStudy);
    }

    /**
     * 주어진 사용자 식별자, 스터디 식별자, 수정할 스터디 정보로 스터디를 수정하고 반환한다.
     *
     * @param email 사용자 식별자
     * @param id 스터디 식별자
     * @param studyUpdateDto 수정할 스터디 정보
     * @return 수정된 스터디 식별자
     * @throws AccountNotManagerOfStudyException 스터디 식별자에 해당하는 스터디의 메일과 사용자 식별자가 다른 경우
     * @throws StudyStartDateInThePastException 스터디 식별자에 해당하는 스터디 시작일이 과거인 경우
     * @throws StudyStartAndEndDateNotValidException 스터디 식별자에 해당하는 스터디 종료일이 시작일보다 빠른 경우
     * @throws StudyStartAndEndTimeNotValidException 스터디 식별자에 해당하는 스터디 종료시간이 시작시간보다 빠른 경우
     */
    public StudyDto.StudyResultDto updateStudy(String email, Long id, StudyDto.StudyUpdateDto studyUpdateDto) {
        Study study = getStudy(id);
        Account loginAccount = accountService.findAccountByEmail(email);
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
        return StudyDto.StudyResultDto.of(study);
    }

    /**
     * 스터디 시작 시간이 종료 시간보다 늦은지 검사하고 반환한다.
     *
     * @param startTime 스터디 시작 시간
     * @param endTime 스터디 종료 시간
     * @return 스터디 시작시간이 종료시간보다 늦는지 여부
     * @throws ParseTimeException 시간 패턴이 잘못된 경우
     */
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

    /**
     * 스터디 시작 날짜가 종료 날짜보다 늦은지 검사하고 반환한다.
     *
     * @param startDate 스터디 시작 날짜
     * @param endDate 스터디 종료 날짜
     * @return 스터디 시작 날짜가 종료 날짜보다 늦는지 여부
     */
    private boolean startDateIsAfterEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }

    /**
     * 스터디 시작 날짜가 오늘이거나 과거인지 검사하고 반환한다.
     *
     * @param startDate 스터디 시작 날짜
     * @return 스터디 시작 날짜가 오늘이거나 과거인지 여부
     */
    private boolean startDateIsTodayOrBefore(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        return startDate.isBefore(today) || startDate.isEqual(today);
    }

    /**
     * 주어진 사용자 식별자와 스터디 식별자에 해당하는 스터디를 삭제하고 반환한다.
     *
     * @param email 사용자 식별자
     * @param id 스터디 식별자
     * @return 삭제된 스터디 식별자
     * @throws AccountNotManagerOfStudyException 스터디 식별자에 해당하는 스터디 이메일과 사용자 식별자가 다른 경우
     */
    public StudyDto.StudyResultDto deleteStudy(String email, Long id) {
        Study study = getStudy(id);
        Account loginAccount = accountService.findAccountByEmail(email);
        if(!study.getEmail().equals(loginAccount.getEmail())) {
            throw new AccountNotManagerOfStudyException();
        }

        study.deleteAccounts();
        studyRepository.delete(study);

        return StudyDto.StudyResultDto.of(study);
    }

    /**
     * 주어진 스터디 식별자로 스터디 지원을 생성하고 아이디를 반환한다.
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 지원한 스터디 식별자
     * @throws StudyNotInOpenStateException 스터디 식별자에 해당하는 스터디가 모집중이 아닌 경우
     * @throws StudyAlreadyExistedException 스터디 식별자에 해당하는 스터디 지원이 이미 존재하는 경우
     * @throws StudySizeFullException 스터디 식별자에 해당하는 스터디 정원이 다 찬 경우
     */
    public StudyApplyResultDto applyStudy(UserAccount userAccount, Long id) {
        Study study = getStudy(id);
        Account account = accountService.findAccountByEmail(userAccount.getAccount().getEmail());

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

        return StudyApplyResultDto.of(id);
    }

    /**
     * 주어진 스터디 식별자로 스터디 신청을 취소하고 아이디를 반환한다.
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 취소한 스터디 식별자
     * @throws StudyNotInOpenStateException 스터디 식별자에 해당하는 스터디가 모집중이 아닌 경우
     * @throws StudyNotAppliedBefore 스터디 식별자에 해당하는 스터디 신청이 존재하지 않는 경우
     */
    public StudyApplyResultDto cancelStudy(UserAccount userAccount, Long id) {
        Study study = getStudy(id);
        Account account = accountService.findAccountByEmail(userAccount.getAccount().getEmail());

        if(!study.getStudyState().equals(StudyState.OPEN)) {
            throw new StudyNotInOpenStateException();
        }

        if(!study.getAccounts().contains(account)) {
            throw new StudyNotAppliedBefore();
        }

        study.cancelAccount(account);

        return StudyApplyResultDto.of(id);
    }

    /**
     * 모든 스터디 리스트를 반환한다.
     *
     * @return 스터디 리스트
     */
    public List<Study> getStudies() {
        return studyRepository.findAll();
    }

    /**
     * 주어진 스터디 식별자에 해당하는 스터디를 반환한다.
     *
     * @param id 스터디 식별자
     * @return 스터디 식별자에 해당하는 스터디
     * @throws StudyNotFoundException 스터디 식별자에 해당하는 스터디가 존재하지 않는 경우
     */
    public Study getStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new StudyNotFoundException(id));
    }

    /**
     * 주어진 스터디 식별자에 해당하는 스터디 정보를 반환한다.
     * 로그인한 사용자의 스터디 즐겨찾기 여부, 댓글, 댓글 좋아요 여부를 포함한다.
     *
     * @param userAccount 로그인한 사용자
     * @param id 스터디 식별자
     * @return 스터디 식별자에 해당하는 스터디 정보
     */
    public StudyDto.StudyDetailResultDto getDetailedStudy(UserAccount userAccount, Long id) {
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

        return StudyDto.StudyDetailResultDto.of(StudyDto.StudyResultDto.of(study), studyCommentResultDtos);
    }

    /**
     * 주어진 검색어와 스터디 상태에 해당하는 스터디 페이징 정보를 반환한다.
     *
     * @param keyword 검색어
     * @param studyState 스터디 상태
     * @param principalId 로그인한 사용자 식별자
     * @param pageable 페이징 정보
     * @return 검색어와 스터디 상태에 해당하는 스터디 페이징 정보
     */
    public Page<StudyDto.StudyResultDto> getStudiesBySearch(String keyword, StudyState studyState, Long principalId, Pageable pageable) {
        List<Study> studies = studyRepository.findByBookNameContaining(keyword, studyState, pageable);
        long total = studyRepository.getStudiesCountByKeyword(keyword, studyState);

        studies.forEach(study -> {
            study.addLikesCount(study.getStudyLikes().size());
            study.getStudyLikes().forEach(studyLike -> {
                if(studyLike.getAccount().getId().equals(principalId)) {
                    study.addLiked();
                }
            });
        });

        List<StudyDto.StudyResultDto> studyResultDtos = studies.stream()
                .map(StudyDto.StudyResultDto::of)
                .collect(Collectors.toList());

        return new PageImpl<>(studyResultDtos, pageable, total);
    }

    /**
     * 주어진 스터디 식별자에 해당하는 스터디 정보를 반환한다.
     * 스터디 참가자 정보를 조회할 때 사용한다.
     *
     * @param id 스터디 식별자
     * @return 스터디 식별자에 해당하는 스터디 정보
     */
    public StudyInfoResultDto getStudyInfo(Long id) {
        return studyRepository.getStudyInfo(id);
    }

    /**
     * 모집중인 스터디의 날짜가 마감되면 스터디 상태를 진행중으로 수정한다.
     * 매일 밤 12시에 스케쥴러로 동작한다.
     */
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

    /**
     * 진행중인 스터디의 날짜가 마감되면 스터디 상태를 종료로 수정한다.
     * 매일 밤 12시에 스케쥴러로 동작한다.
     */
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

    /**
     * 주어진 스터디 상태에 해당하는 스터디 갯수를 반환한다.
     *
     * @param studyState 스터디 상태
     * @return 스터디 상태에 해당하는 스터디 갯수
     */
    public long getStudiesCount(StudyState studyState) {
        return studyRepository.getStudiesCount(studyState);
    }

    /**
     * 모든 스터디 갯수를 반환한다.
     *
     * @return 스터디 갯수
     */
    public long getAllStudiesCount() {
        return studyRepository.getAllStudiesCount();
    }
}
