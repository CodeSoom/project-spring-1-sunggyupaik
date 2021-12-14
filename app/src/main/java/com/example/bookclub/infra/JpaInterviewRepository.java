package com.example.bookclub.infra;

import com.example.bookclub.domain.Interview;
import com.example.bookclub.domain.InterviewRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface JpaInterviewRepository
        extends InterviewRepository, CrudRepository<Interview, Long> {
    Interview save(Interview interview);

    Page<Interview> findAll(Pageable pageable);

    @Query("SELECT i from Interview i ORDER BY i.date DESC")
    List<Interview> findAll();

    Optional<Interview> findByTitle(String title);
}
