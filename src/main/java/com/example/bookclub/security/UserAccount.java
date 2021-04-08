package com.example.bookclub.security;

import com.example.bookclub.domain.Account;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

@Getter
public class UserAccount extends User {
    private final Account account;
    private final List<GrantedAuthority> authorities;

    public UserAccount(Account account, List<GrantedAuthority> authorities) {
        super(account.getEmail(), account.getPassword(), authorities);
        this.account = account;
        this.authorities = authorities;
    }
}
