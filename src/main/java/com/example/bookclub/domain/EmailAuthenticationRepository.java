package com.example.bookclub.domain;

import java.util.Optional;

public interface EmailAuthenticationRepository {
    EmailAuthentication save(EmailAuthentication emailAuthentication);

    Optional<EmailAuthentication> findByEmail(String email);

    void delete(EmailAuthentication emailAuthentication);
}
