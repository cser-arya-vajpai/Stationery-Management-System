package com.stationery.auth.service;

import com.stationery.auth.dto.AuthResponse;
import com.stationery.auth.dto.LoginRequest;
import com.stationery.auth.dto.RegisterRequest;
import com.stationery.auth.exception.UserAlreadyExistsException;
import com.stationery.auth.model.Role;
import com.stationery.auth.model.User;
import com.stationery.auth.repository.UserRepository;
import com.stationery.auth.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest studentRegisterRequest;
    private RegisterRequest adminRegisterRequest;
    private LoginRequest loginRequest;
    private User mockUser;

    @BeforeEach
    void setUp() {
        studentRegisterRequest = new RegisterRequest();
        studentRegisterRequest.setName("Student User");
        studentRegisterRequest.setEmail("student@test.com");
        studentRegisterRequest.setPassword("password123");
        studentRegisterRequest.setRole(Role.STUDENT);

        adminRegisterRequest = new RegisterRequest();
        adminRegisterRequest.setName("Admin User");
        adminRegisterRequest.setEmail("admin@test.com");
        adminRegisterRequest.setPassword("password123");
        adminRegisterRequest.setRole(Role.ADMIN);
        adminRegisterRequest.setAdminSecretCode("STATIONERY_ADMIN_2026");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("student@test.com");
        loginRequest.setPassword("password123");

        mockUser = User.builder()
                .id(1L)
                .name("Student User")
                .email("student@test.com")
                .password("encodedPassword")
                .role(Role.STUDENT)
                .build();
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenValidStudentRequest() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.register(studentRegisterRequest);

        assertNotNull(response);
        assertEquals("student@test.com", response.getEmail());
        assertEquals("mockToken", response.getToken());
        assertEquals("STUDENT", response.getRole());
    }

    @Test
    void register_ShouldReturnAuthResponse_WhenValidAdminRequest() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        
        User adminUser = User.builder()
                .id(2L)
                .name("Admin User")
                .email("admin@test.com")
                .password("encodedPassword")
                .role(Role.ADMIN)
                .build();
        when(userRepository.save(any(User.class))).thenReturn(adminUser);
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.register(adminRegisterRequest);

        assertNotNull(response);
        assertEquals("admin@test.com", response.getEmail());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        when(userRepository.existsByEmail(any())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class,
                () -> authService.register(studentRegisterRequest));
    }

    @Test
    void register_ShouldThrowException_WhenAdminCodeInvalid() {
        adminRegisterRequest.setAdminSecretCode("INVALID_CODE");

        assertThrows(IllegalArgumentException.class,
                () -> authService.register(adminRegisterRequest));
    }

    @Test
    void register_ShouldThrowException_WhenAdminCodeMissing() {
        adminRegisterRequest.setAdminSecretCode(null);

        assertThrows(IllegalArgumentException.class,
                () -> authService.register(adminRegisterRequest));
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenValidCredentials() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(jwtUtil.generateToken(any(), any())).thenReturn("mockToken");

        AuthResponse response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("student@test.com", response.getEmail());
        assertEquals("mockToken", response.getToken());
    }

    @Test
    void login_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIncorrect() {
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        assertThrows(BadCredentialsException.class,
                () -> authService.login(loginRequest));
    }
}