package com.example.app.service.impl;

import com.example.app.dto.AuthRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.model.AuthUser;
import com.example.app.model.User;
import com.example.app.repository.AuthUserRepository;
import com.example.app.repository.UserRepository;
import com.example.app.security.JwtTokenUtil;
import com.example.app.service.AuthService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthUserRepository authUserRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;

    public AuthServiceImpl(
            AuthUserRepository authUserRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenUtil jwtTokenUtil) {
        this.authUserRepository = authUserRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public String login(AuthRequest request) {
        AuthUser authUser = authUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), authUser.getPasswordHash())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return jwtTokenUtil.generateToken(new org.springframework.security.core.userdetails.User(
                authUser.getEmail(),
                authUser.getPasswordHash(),
                new ArrayList<>()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AuthUser register(RegisterRequest request) {
        if (authUserRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        try {
            // Create new User
            User user = new User();
            user.setName(request.getName());
            user.setEmail(request.getEmail());
            user.setUsername(request.getEmail()); // Using email as username
            user.setPhone("");
            user.setWebsite("");
            
            // Save User first
            User savedUser = userRepository.save(user);

            // Create AuthUser
            AuthUser authUser = new AuthUser();
            authUser.setName(request.getName());
            authUser.setEmail(request.getEmail());
            authUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
            authUser.setUser(savedUser);

            // Save AuthUser
            AuthUser savedAuthUser = authUserRepository.save(authUser);

            // Update bidirectional relationship
            savedUser.setAuthUser(savedAuthUser);
            userRepository.save(savedUser);

            return savedAuthUser;
        } catch (Exception e) {
            throw new RuntimeException("Failed to register user: " + e.getMessage(), e);
        }
    }
} 