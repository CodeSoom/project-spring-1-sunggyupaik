package com.example.bookclub.application;

import com.example.bookclub.domain.Day;
import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import com.example.bookclub.domain.Zone;
import com.example.bookclub.dto.StudyCreateDto;
import com.example.bookclub.dto.StudyResultDto;
import com.example.bookclub.dto.StudyUpdateDto;
import com.example.bookclub.errors.StudyNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class StudyServiceTest {
    private static final Long EXISTED_ID = 1L;
    private static final String SETUP_NAME = "name";
    private static final String SETUP_DESCRIPTION = "description";
    private static final String SETUP_CONTACT = "contact";
    private static final int SETUP_SIZE = 5;
    private static final LocalDate SETUP_STARTDATE = LocalDate.now();
    private static final LocalDate SETUP_ENDDATE = LocalDate.now().plusDays(7);
    private static final Day SETUP_DAY = Day.MONDAY;
    private static final String SETUP_STARTTIME = "13:00";
    private static final String SETUP_ENDTIME = "15:30";
    private static final StudyState SETUP_STUDYSTATE = StudyState.OPEN;
    private static final Zone SETUP_ZONE = Zone.SEOUL;

    private static final String UPDATE_NAME = "updatedName";
    private static final String UPDATE_DESCRIPTION = "updatedDescription";
    private static final String UPDATE_CONTACT = "updatedContact";
    private static final int UPDATE_SIZE = 10;
    private static final LocalDate UPDATE_STARTDATE = LocalDate.now().plusDays(1);
    private static final LocalDate UPDATE_ENDDATE = LocalDate.now().plusDays(5);
    private static final Day UPDATE_DAY = Day.THURSDAY;
    private static final String UPDATE_STARTTIME = "12:00";
    private static final String UPDATE_ENDTIME = "14:30";
    private static final StudyState UPDATE_STUDYSTATE = StudyState.CLOSE;
    private static final Zone UPDATE_ZONE = Zone.BUSAN;

    private static final Long NOT_EXISTED_ID = 2L;

    private Study setUpStudy;

    private StudyCreateDto studyCreateDto;
    private StudyUpdateDto studyUpdateDto;

    private StudyService studyService;
    private StudyRepository studyRepository;

    @BeforeEach
    void setUp() {
        studyRepository = mock(StudyRepository.class);
        studyService = new StudyService(studyRepository);

        setUpStudy = Study.builder()
                .id(EXISTED_ID)
                .name(SETUP_NAME)
                .description(SETUP_DESCRIPTION)
                .contact(SETUP_CONTACT)
                .size(SETUP_SIZE)
                .startDate(SETUP_STARTDATE)
                .endDate(SETUP_ENDDATE)
                .startTime(SETUP_STARTTIME)
                .endTime(SETUP_ENDTIME)
                .day(SETUP_DAY)
                .studyState(SETUP_STUDYSTATE)
                .zone(SETUP_ZONE)
                .build();

        studyCreateDto = StudyCreateDto.builder()
                .name(SETUP_NAME)
                .description(SETUP_DESCRIPTION)
                .contact(SETUP_CONTACT)
                .size(SETUP_SIZE)
                .startDate(SETUP_STARTDATE)
                .endDate(SETUP_ENDDATE)
                .startTime(SETUP_STARTTIME)
                .endTime(SETUP_ENDTIME)
                .day(SETUP_DAY)
                .studyState(SETUP_STUDYSTATE)
                .zone(SETUP_ZONE)
                .build();

        studyUpdateDto = StudyUpdateDto.builder()
                .name(UPDATE_NAME)
                .description(UPDATE_DESCRIPTION)
                .contact(UPDATE_CONTACT)
                .size(UPDATE_SIZE)
                .startDate(UPDATE_STARTDATE)
                .endDate(UPDATE_ENDDATE)
                .startTime(UPDATE_STARTTIME)
                .endTime(UPDATE_ENDTIME)
                .day(UPDATE_DAY)
                .studyState(UPDATE_STUDYSTATE)
                .zone(UPDATE_ZONE)
                .build();
    }

    @Test
    void detailWithExistedId() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));

        Study study = studyService.getStudy(EXISTED_ID);

        assertThat(study.getId()).isEqualTo(EXISTED_ID);
    }

    @Test
    void detailWithNotExistedId() {
        given(studyRepository.findById(NOT_EXISTED_ID)).willReturn(Optional.empty());

        assertThatThrownBy(() -> studyService.getStudy(NOT_EXISTED_ID))
                .isInstanceOf(StudyNotFoundException.class);
    }

    @Test
    void createWithValidateAttribute() {
        given(studyRepository.save(any(Study.class))).willReturn(setUpStudy);

        StudyResultDto studyResultDto = studyService.createStudy(studyCreateDto);

        assertThat(studyResultDto.getId()).isEqualTo(setUpStudy.getId());
        assertThat(studyResultDto.getName()).isEqualTo(setUpStudy.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(setUpStudy.getDescription());
    }
    
    @Test
    void updateWithValidateAttribute() {
        given(studyRepository.findById(EXISTED_ID)).willReturn(Optional.of(setUpStudy));

        StudyResultDto studyResultDto = studyService.updateStudy(EXISTED_ID, studyUpdateDto);

        assertThat(studyResultDto.getName()).isEqualTo(studyUpdateDto.getName());
        assertThat(studyResultDto.getDescription()).isEqualTo(studyUpdateDto.getDescription());
    }
}
