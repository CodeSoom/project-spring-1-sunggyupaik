package com.example.bookclub.application;

import com.example.bookclub.domain.Account;
import com.example.bookclub.repository.account.AccountRepositoryCustom;
import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import com.example.bookclub.errors.AccountEmailNotFoundException;
import com.example.bookclub.repository.account.JpaAccountRepository;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class AccountAuthenticationService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final AccountRepositoryCustom accountRepository;

    public AccountAuthenticationService(RoleRepository roleRepository,
                                        JpaAccountRepository accountRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = getAccountByEmail(email);
        List<GrantedAuthority> authorities = getAllAuthorities(email);
        return UserAccount.builder()
                .account(account)
                .authorities(authorities)
                .build();
    }

    public Account getAccountByEmail(String email) {
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
