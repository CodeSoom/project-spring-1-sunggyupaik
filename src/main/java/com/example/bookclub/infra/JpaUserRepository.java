package com.example.bookclub.infra;

import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaUserRepository
        extends UserRepository, CrudRepository<User, Long> {
    Optional<User> findById(Long id);

    User save(User user);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByIdNotAndNickname(Long id, String nickname);
}
