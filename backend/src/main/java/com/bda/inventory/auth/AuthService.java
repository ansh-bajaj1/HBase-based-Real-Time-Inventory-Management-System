package com.bda.inventory.auth;

import com.bda.inventory.exception.ConflictException;
import com.bda.inventory.exception.UnauthorizedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AuthRepository authRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.authRepository = authRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponse register(AuthRequest request) {
        if (authRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new ConflictException("Username already exists");
        }

        AuthUser authUser = new AuthUser();
        authUser.setUsername(request.getUsername());
        authUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        authRepository.save(authUser);

        return new AuthResponse(jwtService.generateToken(request.getUsername()));
    }

    public AuthResponse login(AuthRequest request) {
        AuthUser authUser = authRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Invalid username or password"));

        if (!passwordEncoder.matches(request.getPassword(), authUser.getPasswordHash())) {
            throw new UnauthorizedException("Invalid username or password");
        }

        return new AuthResponse(jwtService.generateToken(authUser.getUsername()));
    }
}
