package com.example.bookclub.application;

import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;

    public AuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SessionResultDto createToken(SessionCreateDto sessionCreateDto) {
        User user = authenticateUser(sessionCreateDto);

        return SessionResultDto.builder()
                .accessToken("setUpaccessToken")
                .build();
    }

    public User authenticateUser(SessionCreateDto sessionCreateDto) {
        String email = sessionCreateDto.getEmail();
        String password = sessionCreateDto.getPassword();

        return userRepository.findByEmail(email)
                .filter(u -> u.authenticate(password))
                .orElseThrow(AuthenticationBadRequestException::new);
    }
}
