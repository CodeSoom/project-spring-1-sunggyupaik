package com.example.bookclub.application;

import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import com.example.bookclub.utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public SessionResultDto createToken(SessionCreateDto sessionCreateDto) {
        User user = authenticateUser(sessionCreateDto);

        String accessToken = jwtUtil.encode(user.getId(), user.getEmail());

        return SessionResultDto.of(accessToken);
    }

    public User authenticateUser(SessionCreateDto sessionCreateDto) {
        String email = sessionCreateDto.getEmail();
        String password = sessionCreateDto.getPassword();

        return userRepository.findByEmail(email)
                .filter(u -> u.authenticate(password))
                .orElseThrow(AuthenticationBadRequestException::new);
    }
}
