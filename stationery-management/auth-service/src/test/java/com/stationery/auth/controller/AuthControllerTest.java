package com.stationery.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stationery.auth.dto.AuthResponse;
import com.stationery.auth.dto.LoginRequest;
import com.stationery.auth.dto.RegisterRequest;
import com.stationery.auth.model.Role;
import com.stationery.auth.service.AuthService;
import com.stationery.auth.security.JwtUtil; // Added Import
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil; // Mocked JwtUtil to satisfy JwtFilter dependency

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private AuthResponse authResponse;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setName("Student Name");
        registerRequest.setEmail("student@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(Role.STUDENT);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("student@test.com");
        loginRequest.setPassword("password123");

        authResponse = AuthResponse.builder()
                .token("mockJwtToken")
                .email("student@test.com")
                .name("Student Name")
                .role("STUDENT")
                .message("Success")
                .build();
    }

    @Test
    void register_ShouldReturnCreatedStatus() throws Exception {
        when(authService.register(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("student@test.com"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
    }

    @Test
    void login_ShouldReturnOkStatus() throws Exception {
        when(authService.login(any())).thenReturn(authResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("student@test.com"));
    }

    @Test
    void health_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/auth/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Auth Service is running"));
    }
}
