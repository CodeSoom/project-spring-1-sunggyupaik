package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.StartAndEndDateNotValidException;
import com.example.bookclub.errors.StartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyAlreadyExistedException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import com.example.bookclub.errors.StudyStartDateInThePastException;
import com.example.bookclub.security.UserAccount;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
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

    public StudyResultDto createStudy(Account account,
                                      StudyCreateDto studyCreateDto)
            throws ParseException {
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        if(account.getStudy() != null) {
            throw new StudyAlreadyExistedException();
        }

        if(startDateIsTodayOrBefore(studyCreateDto.getStartDate())) {
            throw new StudyStartDateInThePastException();
        }

        if(startDateIsAfterEndDate(studyCreateDto.getStartDate(),
                studyCreateDto.getEndDate())) {
            throw new StartAndEndDateNotValidException();
        }

        if(startTimeIsAfterEndTime(studyCreateDto.getStartTime(),
                studyCreateDto.getEndTime())) {
            throw new StartAndEndTimeNotValidException();
        }

        Study study = studyCreateDto.toEntity();
        Study createdStudy = studyRepository.save(study);
        createdStudy.addAdmin(account);
        login(account);

        return StudyResultDto.of(createdStudy);
    }

    public StudyResultDto updateStudy(Account account,
                                      Long id,
                                      StudyUpdateDto studyUpdateDto) throws ParseException {
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        Study study = getStudy(id);
        if (!study.isManagedBy(account)) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        if(startDateIsTodayOrBefore(studyUpdateDto.getStartDate())) {
            throw new StudyStartDateInThePastException();
        }

        if(startDateIsAfterEndDate(studyUpdateDto.getStartDate(),
                studyUpdateDto.getEndDate())) {
            throw new StartAndEndDateNotValidException();
        }

        if(startTimeIsAfterEndTime(studyUpdateDto.getStartTime(),
                studyUpdateDto.getEndTime())) {
            throw new StartAndEndTimeNotValidException();
        }

        study.updateWith(studyUpdateDto);
        return StudyResultDto.of(study);
    }

    private boolean startTimeIsAfterEndTime(String startTime, String endTime) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTimeDate = sdf.parse(startTime);
        Date endTimeDate = sdf.parse(endTime);
        return startTimeDate.after(endTimeDate);
    }

    private boolean startDateIsAfterEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }

    private boolean startDateIsTodayOrBefore(LocalDate startDate) {
        LocalDate today = LocalDate.now();
        return startDate.isBefore(today) || startDate.isEqual(today);
    }

    public StudyResultDto deleteStudy(Account account, Long id) {
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        Study study = getStudy(id);
        if(!study.isManagedBy(account)) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        study.deleteAccounts();
        studyRepository.delete(study);

        return StudyResultDto.of(study);
    }

    public Long applyStudy(Account account, Long id) {
        if(account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        if(account.getStudy() != null) {
            throw new StudyAlreadyExistedException();
        }

        Study study = getStudy(id);
        if (study.isSizeFull()){
            throw new StudySizeFullException();
        }

        study.addAccount(account);
        login(account);

        return id;
    }

    public Long cancelStudy(Account account, Long id) {
        Study study = getStudy(id);
        Account user = getAccount(account.getId());
        study.cancelAccount(user);
        login(user);

        return id;
    }

    public Study getStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(StudyNotFoundException::new);
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public List<Study> getStudies() {
        return studyRepository.findAll();
    }

    public List<Study> getStudiesBySearch(String keyword) {
        return studyRepository.findByKeyword(keyword);
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

        for (Study study : lists) {
            LocalDate studyEndDate = study.getStartDate();
            LocalDate nowDate = LocalDate.now();
            if (studyEndDate.isEqual(nowDate)) {
               study.changeOpenToClose();
            }
        }
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleCloseToEnd() {
        List<Study> lists = getStudies().stream()
                .filter(s -> s.getStudyState().equals(StudyState.CLOSE))
                .collect(Collectors.toList());

        for (Study study : lists) {
            LocalDate studyEndDate = study.getEndDate();
            LocalDate nowDate = LocalDate.now();
            if (nowDate.isAfter(studyEndDate)) {
                study.changeCloseToEnd();
            }
        }
    }

    public void login(Account account) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("USER"));

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                new UserAccount(account, authorities),
                account.getPassword(),
                authorities);
        SecurityContextHolder.getContext().setAuthentication(token);
    }
}
