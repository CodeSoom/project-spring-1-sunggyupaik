package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.ParseTimeException;
import com.example.bookclub.errors.StartAndEndDateNotValidException;
import com.example.bookclub.errors.StartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
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
    private final StudyRepository studyRepository;
    private final AccountRepository accountRepository;

    public StudyService(StudyRepository studyRepository,
                        AccountRepository accountRepository) {
        this.studyRepository = studyRepository;
        this.accountRepository = accountRepository;
    }

    public StudyResultDto createStudy(Account account, StudyCreateDto studyCreateDto) {
        if (startDateIsTodayOrBefore(studyCreateDto.getStartDate())) {
            throw new StudyStartDateInThePastException();
        }

        if (startDateIsAfterEndDate(studyCreateDto.getStartDate(),
                studyCreateDto.getEndDate())) {
            throw new StartAndEndDateNotValidException();
        }

        if (startTimeIsAfterEndTime(studyCreateDto.getStartTime(),
                studyCreateDto.getEndTime())) {
            throw new StartAndEndTimeNotValidException();
        }

        Account loginAccount = accountRepository.findByEmail(account.getEmail()).get();
        Study study = studyCreateDto.toEntity();
        study.addAdmin(loginAccount);
        Study createdStudy = studyRepository.save(study);

        return StudyResultDto.of(createdStudy);
    }

    public StudyResultDto updateStudy(Account account,
                                      Long id,
                                      StudyUpdateDto studyUpdateDto) {
        Study study = getStudy(id);

        if (startDateIsTodayOrBefore(studyUpdateDto.getStartDate())) {
            throw new StudyStartDateInThePastException();
        }

        if (startDateIsAfterEndDate(studyUpdateDto.getStartDate(),
                studyUpdateDto.getEndDate())) {
            throw new StartAndEndDateNotValidException();
        }

        if (startTimeIsAfterEndTime(studyUpdateDto.getStartTime(),
                studyUpdateDto.getEndTime())) {
            throw new StartAndEndTimeNotValidException();
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

    public StudyResultDto deleteStudy(Account account, Long id) {
        Study study = getStudy(id);
        study.deleteAccounts();
        studyRepository.delete(study);

        return StudyResultDto.of(study);
    }

    public Long applyStudy(Account account, Long id) {
        if (account.getStudy() != null) {
            throw new StudyAlreadyExistedException();
        }

        Study study = getStudy(id);
        if (study.isSizeFull()){
            throw new StudySizeFullException();
        }

        study.addAccount(account);

        return id;
    }

    public Long cancelStudy(Account account, Long id) {
        Study study = getStudy(id);
        study.cancelAccount(account);

        return id;
    }

    public Study getStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new StudyNotFoundException(id));
    }

    public List<Study> getStudies() {
        return studyRepository.findAll();
    }

    public List<Study> getStudiesBySearch(String keyword) {
        return studyRepository.findByBookNameContaining(keyword);
    }

    public List<Study> getStudiesByStudyState(StudyState studyState) {
        return studyRepository.findByStudyState(studyState);
    }

    public long countAllStudies() {
        return getStudies().size();
    }

    public long countCloseStudies() {
        return getStudiesByStudyState(StudyState.CLOSE).size();
    }

    public long countEndStudies() {
        return getStudiesByStudyState(StudyState.END).size();
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
}
