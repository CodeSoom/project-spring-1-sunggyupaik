package com.example.bookclub.infra.account;

import com.example.bookclub.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepository
        extends AccountRepositoryCustom, JpaRepository<Account, Long> {
    Optional<Account> findById(Long id);

    Optional<Account> findByEmail(String email);

    Account save(Account account);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByIdNotAndNickname(Long id, String nickname);

    List<Account> findAll();
}
