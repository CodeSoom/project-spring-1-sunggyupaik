package com.example.bookclub.domain;

import java.util.Optional;

public interface AccountRepository {
    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);

    Account save(Account account);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByIdNotAndNickname(Long id, String nickname);
}
