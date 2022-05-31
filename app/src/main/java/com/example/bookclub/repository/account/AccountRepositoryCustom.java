package com.example.bookclub.repository.account;

import com.example.bookclub.domain.account.Account;

import java.util.Optional;

public interface AccountRepositoryCustom {
    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);

	long getAllAccountsCount();
}
