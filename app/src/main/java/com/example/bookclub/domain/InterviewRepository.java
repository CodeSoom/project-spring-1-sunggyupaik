package com.example.bookclub.domain;

import java.util.List;

public interface InterviewRepository {
    Interview save(Interview interview);

    List<Interview> findAll();
}
