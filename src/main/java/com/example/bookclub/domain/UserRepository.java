package com.example.bookclub.domain;

import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);

    User save(User user);

    boolean existsByEmail(String email);
}
