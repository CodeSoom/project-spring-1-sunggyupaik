package com.example.bookclub.application;

import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.domain.User;
import com.example.bookclub.domain.UserRepository;
import com.example.bookclub.dto.ParseResultDto;
import com.example.bookclub.dto.SessionCreateDto;
import com.example.bookclub.dto.SessionResultDto;
import com.example.bookclub.errors.AuthenticationBadRequestException;
import com.example.bookclub.errors.InvalidTokenException;
import com.example.bookclub.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;

    public AuthenticationService(UserRepository userRepository,
                                 RoleRepository roleRepository,
                                 JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
    }

    public SessionResultDto createToken(SessionCreateDto sessionCreateDto) {
        User user = authenticateUser(sessionCreateDto);

        String accessToken = jwtUtil.encode(user.getId(), user.getEmail());

        return SessionResultDto.of(accessToken);
    }

    public ParseResultDto parseToken(String token) {
        if(token == null || token.isBlank()) {
            throw new InvalidTokenException(token);
        }

        try {
            Claims claims = jwtUtil.decode(token);
            return ParseResultDto.of(claims);
        } catch(SignatureException e) {
            throw new InvalidTokenException(token);
        }
    }

    public User authenticateUser(SessionCreateDto sessionCreateDto) {
        String email = sessionCreateDto.getEmail();
        String password = sessionCreateDto.getPassword();

        return userRepository.findByEmail(email)
                .filter(u -> u.authenticate(password))
                .orElseThrow(AuthenticationBadRequestException::new);
    }

    public List<Role> roles(String email) {
        return roleRepository.findAllByEmail(email);
    }
}
