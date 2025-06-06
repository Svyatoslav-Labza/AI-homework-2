package com.example.app.service;

import com.example.app.model.User;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import java.util.List;
import java.util.Optional;

@Validated
public interface UserService {
    // Create
    User createUser(@Valid User user);

    // Read
    List<User> getAllUsers();
    Optional<User> getUserById(Long id);

    // Update
    User updateUser(Long id, @Valid User user);

    // Delete
    void deleteUser(Long id);
} 