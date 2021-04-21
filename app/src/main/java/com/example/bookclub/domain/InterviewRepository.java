package com.example.bookclub.domain;

import java.util.List;

public interface InterviewRepository {
    List<Interview> saveAll(List<Interview> list);
}
