package com.example.bookclub.infrastructure.account.role;

import com.example.bookclub.domain.account.role.Role;
import com.example.bookclub.domain.account.role.RoleRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaRoleRepository
        extends RoleRepository, CrudRepository<Role, Long> {
    List<Role> findAllByEmail(String email);
}
