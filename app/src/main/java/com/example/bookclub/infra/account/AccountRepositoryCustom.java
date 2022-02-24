package com.example.bookclub.infra.account;

import com.example.bookclub.domain.Account;

import java.util.Optional;

public interface AccountRepositoryCustom {
    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);
}
