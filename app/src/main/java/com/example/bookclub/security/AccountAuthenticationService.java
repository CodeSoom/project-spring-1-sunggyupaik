package com.example.bookclub.security;

import com.example.bookclub.domain.Account;
import com.example.bookclub.domain.AccountRepository;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountAuthenticationService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;

    public AccountAuthenticationService(RoleRepository roleRepository,
                                        AccountRepository accountRepository,
                                        PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    public void login(SessionCreateDto sessionCreateDto) {
//        Account account = authenticateUser(sessionCreateDto); // LAZY 강제 초기화를 위한 연관관계 내부 명시적 조인?
//        //account.getUploadFile().getFileUrl(); // LAZY 강제 초기화를 위한 연관관계 조회
//        List<GrantedAuthority> authorities = getAllAuthorities(sessionCreateDto.getEmail());
//
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
//                new UserAccount(account, authorities), account.getPassword(), authorities);
//        SecurityContextHolder.getContext().setAuthentication(token);
//    }

//    public Account authenticateUser(SessionCreateDto sessionCreateDto) {
//        String email = sessionCreateDto.getEmail();
//        String password = sessionCreateDto.getPassword();
//
//        return accountRepository.findByEmail(email)
//                .filter(account -> account.authenticate(password, passwordEncoder))
//                .orElseThrow(AuthenticationBadRequestException::new);
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = getAccount(email);
        List<GrantedAuthority> authorities = getAllAuthorities(email);
        return UserAccount.builder()
                .account(account)
                .authorities(authorities)
                .build();
    }

    public Account getAccount(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountEmailNotFoundException(email));
    }

    public List<GrantedAuthority> getAllAuthorities(String email) {
        List<Role> roles = roleRepository.findAllByEmail(email);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
