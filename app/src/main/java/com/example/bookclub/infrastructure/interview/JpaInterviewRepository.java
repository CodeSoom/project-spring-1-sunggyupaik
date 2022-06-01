package com.example.bookclub.infrastructure.interview;

import com.example.bookclub.domain.interview.Interview;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaInterviewRepository
        extends InterviewRepositoryCustom, CrudRepository<Interview, Long> {
    Interview save(Interview interview);

    Optional<Interview> findByTitle(String title);
}
