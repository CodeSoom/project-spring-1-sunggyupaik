package com.example.bookclub.infra;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaAccountRepository
        extends AccountRepository, CrudRepository<Account, Long> {
    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);

    Account save(Account account);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByIdNotAndNickname(Long id, String nickname);
}
