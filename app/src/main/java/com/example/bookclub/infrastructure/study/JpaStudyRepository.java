package com.example.bookclub.infrastructure.study;

import com.example.bookclub.domain.study.Study;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import javax.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

public interface JpaStudyRepository
        extends StudyRepositoryCustom, JpaRepository<Study, Long> {
    Optional<Study> findById(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from Study s where s.id = :id")
    Optional<Study> findByIdForUpdate(Long id);

    Optional<Study> findByEmail(String email);

    List<Study> findAll();

    Study save(Study study);

    void delete(Study study);
}
