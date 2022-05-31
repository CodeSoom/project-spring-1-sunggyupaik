package com.example.bookclub.repository;

import com.example.bookclub.domain.account.emailauthentication.EmailAuthentication;
import com.example.bookclub.domain.account.emailauthentication.EmailAuthenticationRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface JpaEmailAuthenticationRepository
        extends EmailAuthenticationRepository, CrudRepository<EmailAuthentication, Long> {
    EmailAuthentication save(EmailAuthentication emailAuthentication);

    Optional<EmailAuthentication> findByEmail(String email);

    void delete(EmailAuthentication emailAuthentication);
}
