package com.example.bookclub.application.account;

import com.example.bookclub.common.exception.account.AccountEmailNotFoundException;
import com.example.bookclub.domain.account.Account;
import com.example.bookclub.domain.account.role.Role;
import com.example.bookclub.domain.account.role.RoleRepository;
import com.example.bookclub.infrastructure.account.JpaAccountRepository;
import com.example.bookclub.security.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 사용자 조회, 시큐리티 인증, 권한을 반환한다
 */
@Service
public class AccountAuthenticationService implements UserDetailsService {
    private final RoleRepository roleRepository;
    private final JpaAccountRepository accountRepository;

    public AccountAuthenticationService(RoleRepository roleRepository,
                                        JpaAccountRepository accountRepository) {
        this.roleRepository = roleRepository;
        this.accountRepository = accountRepository;
    }

    /**
     * 주어진 이메일로 시큐리티 계정을 반환한다
     * 권한도 조회해서 사용자 정보에 넣어준다
     *
     * @param email 사용자 이메일 식별자
     * @return 사용자
     * @throws UsernameNotFoundException 주어진 이메일에 해당하는 사용자가 없는 경우
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Account account = getAccountByEmail(email);
        List<GrantedAuthority> authorities = getAllAuthorities(email);
        return UserAccount.builder()
                .account(account)
                .authorities(authorities)
                .build();
    }

    /**
     * 주어진 이메일로 사용자를 반환한다
     *
     * @param email 사용자 이메일 식별자
     * @return 사용자
     * @throws AccountEmailNotFoundException 주어진 이메일에 해당하는 사용자가 없는 경우
     */
    @Transactional(readOnly = true)
    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new AccountEmailNotFoundException(email));
    }

    /**
     * 주어진 이메일로 권한을 반환한다
     *
     * @param email 사용자 이메일 식별자
     * @return 권한
     */
    @Transactional(readOnly = true)
    public List<GrantedAuthority> getAllAuthorities(String email) {
        List<Role> roles = roleRepository.findAllByEmail(email);
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
