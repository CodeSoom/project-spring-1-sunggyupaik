package com.example.bookclub.application;

import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.utils.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AccountRepository accountRepository,
                                 RoleRepository roleRepository,
                                 JwtUtil jwtUtil,
                                 PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.roleRepository = roleRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

//    public SessionResultDto createToken(SessionCreateDto sessionCreateDto) {
//        Account account = authenticateUser(sessionCreateDto);
//
//        String accessToken = jwtUtil.encode(account.getId(), account.getEmail());
//
//        return SessionResultDto.of(accessToken);
//    }

//    public ParseResultDto parseToken(String token) {
//        if (token == null || token.isBlank()) {
//            throw new InvalidTokenException(token);
//        }
//
//        try {
//            Claims claims = jwtUtil.decode(token);
//            return ParseResultDto.of(claims);
//        } catch(SignatureException e) {
//            throw new InvalidTokenException(token);
//        }
//    }

//    public Account authenticateUser(SessionCreateDto sessionCreateDto) {
//        String email = sessionCreateDto.getEmail();
//        String password = sessionCreateDto.getPassword();
//
//        return accountRepository.findByEmail(email)
//                .filter(u -> u.authenticate(password, passwordEncoder))
//                .orElseThrow(AuthenticationBadRequestException::new);
//    }

//    public List<Role> roles(String email) {
//        return roleRepository.findAllByEmail(email);
//    }
}
