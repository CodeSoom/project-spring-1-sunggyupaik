package com.example.bookclub.infra;

import com.example.bookclub.domain.EmailAuthentication;
import com.example.bookclub.domain.EmailAuthenticationRepository;
import org.springframework.data.repository.CrudRepository;

public interface JpaEmailAuthenticationRepository
        extends EmailAuthenticationRepository, CrudRepository<EmailAuthentication, Long> {
    EmailAuthentication save(EmailAuthentication emailAuthentication);
}
