package com.bda.inventory.auth;

import java.util.Optional;

public interface AuthRepository {

    Optional<AuthUser> findByUsername(String username);

    void save(AuthUser authUser);
}
