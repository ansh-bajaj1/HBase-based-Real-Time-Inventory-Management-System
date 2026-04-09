package com.bda.inventory.auth;

import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
@Profile("!hbase-auth")
public class InMemoryAuthRepository implements AuthRepository {

    private final ConcurrentHashMap<String, AuthUser> users = new ConcurrentHashMap<>();

    public InMemoryAuthRepository() {
        // Demo-friendly default admin user.
        AuthUser admin = new AuthUser();
        admin.setUsername("admin");
        admin.setPasswordHash(new BCryptPasswordEncoder().encode("admin"));
        users.put(admin.getUsername(), admin);
    }

    @Override
    public Optional<AuthUser> findByUsername(String username) {
        return Optional.ofNullable(users.get(username));
    }

    @Override
    public void save(AuthUser authUser) {
        users.put(authUser.getUsername(), authUser);
    }
}
