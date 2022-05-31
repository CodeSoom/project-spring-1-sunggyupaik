package com.example.bookclub.domain.account.role;

import java.util.List;

public interface RoleRepository {
    List<Role> findAllByEmail(String email);

    Role save(Role role);
}
