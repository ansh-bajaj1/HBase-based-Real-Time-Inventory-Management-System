package com.bda.inventory.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("!hbase-auth")
public class InMemoryAuthRepository implements AuthRepository {

    private final ConcurrentHashMap<String, AuthUser> users = new ConcurrentHashMap<>();

    @Override
    public Optional<AuthUser> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public void save(AuthUser authUser) {
        users.put(authUser.getUsername(), authUser);
    }
}
