package com.example.bookclub.repository.account.accounthistory;

import com.example.bookclub.domain.account.accounthistory.AccountHistory;
import com.example.bookclub.domain.account.accounthistory.AccountHistoryRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaAccountHistoryRepository
		extends AccountHistoryRepository, CrudRepository<AccountHistory, Long> {
	AccountHistory save(AccountHistory accountHistory);

	Optional<AccountHistory> findById(Long id);
}
