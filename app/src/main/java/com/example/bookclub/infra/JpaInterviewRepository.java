package com.example.bookclub.infra;

import com.example.bookclub.domain.Interview;
import com.example.bookclub.domain.InterviewRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaInterviewRepository
        extends InterviewRepository, CrudRepository<Interview, Long> {
    List<Interview> saveAll(List<Interview> list);
}
