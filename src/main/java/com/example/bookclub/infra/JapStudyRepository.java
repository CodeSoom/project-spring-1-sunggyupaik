package com.example.bookclub.infra;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import org.springframework.data.repository.CrudRepository;

public interface JapStudyRepository
        extends StudyRepository, CrudRepository<Study, Long> {
}
