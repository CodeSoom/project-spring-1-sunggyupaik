package com.example.bookclub.repository;

import com.example.bookclub.domain.AccountHistory;
import com.example.bookclub.domain.AccountHistoryRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaAccountHistoryRepository
		extends AccountHistoryRepository, CrudRepository<AccountHistory, Long> {
	AccountHistory save(AccountHistory accountHistory);

	Optional<AccountHistory> findById(Long id);
}
