package com.example.bookclub.security;

import com.example.bookclub.domain.Role;
import lombok.Builder;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

public class UserAuthentication extends AbstractAuthenticationToken {
    private final Long id;
    private final String email;

    @Builder
    public UserAuthentication(Long id, String email, List<Role> roles) {
        super(authorities(roles));
        this.id = id;
        this.email = email;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return this.email;
    }

    @Override
    public boolean isAuthenticated() {
        return true;
    }

    public String getEmail() {
        return this.email;
    }

    public Long getId() {
        return this.id;
    }

    private static List<GrantedAuthority> authorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
