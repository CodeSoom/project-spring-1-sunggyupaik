package com.example.bookclub.domain.account.accounthistory;

import java.util.Optional;

public interface AccountHistoryRepository {
	AccountHistory save(AccountHistory accountHistory);

	Optional<AccountHistory> findById(Long id);

	AccountHistory saveAndFlush(AccountHistory accountHistory);
}
