package com.example.bookclub.infra;

import com.example.bookclub.domain.AccountHistory;
import com.example.bookclub.domain.AccountHistoryRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaAccountHistoryRepository
		extends AccountHistoryRepository, CrudRepository<AccountHistory, Long> {
}
