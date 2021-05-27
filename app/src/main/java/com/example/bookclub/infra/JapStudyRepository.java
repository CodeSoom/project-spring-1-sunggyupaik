package com.example.bookclub.infra;

import com.example.bookclub.domain.Study;
import com.example.bookclub.domain.StudyRepository;
import com.example.bookclub.domain.StudyState;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JapStudyRepository
        extends StudyRepository, CrudRepository<Study, Long> {
    Optional<Study> findById(Long id);

    Optional<Study> findByEmail(String email);

    List<Study> findAll();

    List<Study> findByStudyState(StudyState studyState);

    Study save(Study study);

    void delete(Study study);

//    @Query("SELECT s FROM Study s WHERE s.bookName like %?1%")
    List<Study> findByBookNameContaining(String keyword);
}
