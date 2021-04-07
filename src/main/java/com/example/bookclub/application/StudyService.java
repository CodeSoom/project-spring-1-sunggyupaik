package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.AccountNotFoundException;
import com.example.bookclub.errors.StartAndEndDateNotValidException;
import com.example.bookclub.errors.StartAndEndTimeNotValidException;
import com.example.bookclub.errors.StudyNotFoundException;
import com.example.bookclub.errors.StudySizeFullException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

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
        if (account == null) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        LocalDate startDate = studyCreateDto.getStartDate();
        LocalDate endDate = studyCreateDto.getEndDate();
        LocalDate todayDate = LocalDate.now();
        if(startDate.isBefore(todayDate) || startDate.isAfter(endDate)) {
            throw new StartAndEndDateNotValidException();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date startTime = sdf.parse(studyCreateDto.getStartTime());
        Date endTime = sdf.parse(studyCreateDto.getEndTime());
        if(startTime.after(endTime)) {
            throw new StartAndEndTimeNotValidException();
        }

        Study study = studyCreateDto.toEntity();
        Study createdStudy = studyRepository.save(study);
        createdStudy.addAdmin(account.getEmail());

        return StudyResultDto.of(createdStudy);
    }

    public StudyResultDto updateStudy(Account account,
                                      Long id,
                                      StudyUpdateDto studyUpdateDto) {
        Study study = getStudy(id);
        if (!study.isManagedBy(account)) {
           throw new AccessDeniedException("권한이 없습니다");
        }

        study.updateWith(studyUpdateDto);
        return StudyResultDto.of(study);
    }

    public StudyResultDto deleteStudy(Account account, Long id) {
        Study study = getStudy(id);
        if(!study.isManagedBy(account)) {
            throw new AccessDeniedException("권한이 없습니다");
        }

        studyRepository.delete(study);
        return StudyResultDto.of(study);
    }

    public Long applyStudy(Account account, Long id) {
        Study study = getStudy(id);
        if (study.isSizeFull()){
            throw new StudySizeFullException();
        }
        Account user = getAccount(account.getId());

        study.addAccount(user);

        return id;
    }

    public Long cancelStudy(Account account, Long id) {
        Study study = getStudy(id);
        Account user = getAccount(account.getId());

        study.cancelAccount(user);

        return id;
    }

    public Study getStudy(Long id) {
        return studyRepository.findById(id)
                .orElseThrow(() -> new StudyNotFoundException(id));
    }

    public Account getAccount(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new AccountNotFoundException(id));
    }

    public List<Study> getStudies() {
        return studyRepository.findAll();
    }
}
