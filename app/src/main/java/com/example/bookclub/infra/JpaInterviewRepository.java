package com.example.bookclub.infra;

import com.example.bookclub.domain.InterviewRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaInterviewRepository
        extends InterviewRepository, CrudRepository<Interview, Long> {
    List<Interview> saveAll(List<Interview> list);
}
