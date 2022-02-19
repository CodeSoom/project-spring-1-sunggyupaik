package com.example.bookclub.domain;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);
}
