package com.example.bookclub.infra;

import com.example.bookclub.domain.Role;
import com.example.bookclub.domain.RoleRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JpaRoleRepository
        extends RoleRepository, CrudRepository<Role, Long> {
    List<Role> findAllByEmail(String email);
}
