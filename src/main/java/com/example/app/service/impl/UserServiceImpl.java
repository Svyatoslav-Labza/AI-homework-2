package com.example.app.service.impl;

import com.example.app.exception.UserNotFoundException;
import com.example.app.model.User;
import com.example.app.repository.UserRepository;
import com.example.app.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Create
    @Override
    public User createUser(@Valid User user) {
        return userRepository.save(user);
    }

    // Read
    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Update
    @Override
    public User updateUser(Long id, @Valid User user) {
        return userRepository.findById(id)
            .map(existing -> {
                user.setId(id);
                return userRepository.save(user);
            })
            .orElseThrow(() -> new UserNotFoundException(id));
    }

    // Delete
    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }
} 