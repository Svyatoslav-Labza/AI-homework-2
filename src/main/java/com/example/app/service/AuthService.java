package com.example.app.service;

import com.example.app.dto.AuthRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.model.AuthUser;

public interface AuthService {
    String login(AuthRequest request);
    AuthUser register(RegisterRequest request);
} 