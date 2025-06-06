package com.example.app.integration;

import com.example.app.config.TestConfig;
import com.example.app.dto.AuthRequest;
import com.example.app.dto.RegisterRequest;
import com.example.app.model.AuthUser;
import com.example.app.repository.AuthUserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Import(TestConfig.class)
public class AuthIntegrationTest {

    @Container
    @Autowired
    private PostgreSQLContainer<?> postgreSQLContainer;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AuthUserRepository authUserRepository;

    private RegisterRequest validRegisterRequest;
    private AuthRequest validAuthRequest;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", () -> "jdbc:tc:postgresql:15-alpine:///testdb");
        registry.add("spring.datasource.username", () -> "test");
        registry.add("spring.datasource.password", () -> "test");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
    }

    @BeforeEach
    void setUp() {
        // Clean the database before each test
        authUserRepository.deleteAll();

        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setName("Test User");
        validRegisterRequest.setEmail("test@example.com");
        validRegisterRequest.setPassword("password123");

        validAuthRequest = new AuthRequest();
        validAuthRequest.setEmail("test@example.com");
        validAuthRequest.setPassword("password123");
    }

    @Test
    void fullAuthenticationFlow() throws Exception {
        // Step 1: Register a new user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(validRegisterRequest.getEmail()))
                .andExpect(jsonPath("$.name").value(validRegisterRequest.getName()));

        // Step 2: Verify user exists in database
        AuthUser savedUser = authUserRepository.findByEmail(validRegisterRequest.getEmail())
                .orElseThrow(() -> new AssertionError("User not found in database"));

        // Step 3: Login with the registered user
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void registerWithExistingEmailShouldFail() throws Exception {
        // First registration
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk());

        // Second registration with same email
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Email already registered"));
    }

    @Test
    void loginWithNonExistentUserShouldFail() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validAuthRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithWrongPasswordShouldFail() throws Exception {
        // Register user
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRegisterRequest)))
                .andExpect(status().isOk());

        // Try to login with wrong password
        AuthRequest wrongPasswordRequest = new AuthRequest();
        wrongPasswordRequest.setEmail(validAuthRequest.getEmail());
        wrongPasswordRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(wrongPasswordRequest)))
                .andExpect(status().isUnauthorized());
    }
} 