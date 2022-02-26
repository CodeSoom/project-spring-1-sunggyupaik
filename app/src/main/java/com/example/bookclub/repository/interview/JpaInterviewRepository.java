package com.example.bookclub.repository.interview;

import com.example.bookclub.domain.Interview;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaInterviewRepository
        extends InterviewRepositoryCustom, CrudRepository<Interview, Long> {
    Interview save(Interview interview);

    Optional<Interview> findByTitle(String title);
}
