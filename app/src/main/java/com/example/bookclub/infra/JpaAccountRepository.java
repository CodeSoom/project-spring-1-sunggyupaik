package com.example.bookclub.infra;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JpaAccountRepository
        extends AccountRepository, CrudRepository<Account, Long> {
    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.study s LEFT JOIN FETCH a.uploadFile  WHERE a.id = :id")
    Optional<Account> findById(@Param("id") Long id);

    @Query("SELECT DISTINCT a FROM Account a LEFT JOIN FETCH a.study s LEFT JOIN FETCH a.uploadFile  WHERE a.email = :email")
    Optional<Account> findByEmail(@Param("email") String email);

    Account save(Account account);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByIdNotAndNickname(Long id, String nickname);

    List<Account> findAll();
}
