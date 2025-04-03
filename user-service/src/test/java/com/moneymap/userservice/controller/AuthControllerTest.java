package com.moneymap.userservice.controller;

import com.moneymap.userservice.controller.AuthController;
import com.moneymap.userservice.model.AuthResponse;
import com.moneymap.userservice.model.RegisterRequest;
import com.moneymap.userservice.model.User;
import com.moneymap.userservice.model.UserRole;
import com.moneymap.userservice.security.JwtUtil;
import com.moneymap.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.argThat;

@SpringBootTest
public class AuthControllerTest {

    @InjectMocks
    AuthController authController;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testLoginSuccess(){
        //Mock behavior
        RegisterRequest registerRequest = new RegisterRequest("testuser", "test@gmail.com", "test123", UserRole.USER);

        User newuser = new User();
        newuser.setId(1L);
        newuser.setUsername(registerRequest.getUsername());
        newuser.setEmail(registerRequest.getEmail());
        newuser.setPassword(registerRequest.getPassword());
        newuser.setRole(registerRequest.getRole());

        when(userService.registerUser(any(User.class))).thenReturn(newuser);
        when(jwtUtil.generateToken(any(User.class))).thenReturn("mock-user-token");

        ResponseEntity<?> response = authController.register(registerRequest);
        assertEquals(200, response.getStatusCode().value());
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("mock-user-token", authResponse.getToken());
    }

    @Test
    void testLoginDuplicateUsernameFailure(){
        //Mock behavior
        RegisterRequest registerRequest = new RegisterRequest("existingUser", "test@gmail.com", "test123", UserRole.USER);

        when(userService.registerUser(any(User.class)))
            .thenThrow(new IllegalArgumentException("Username already exists"));

        ResponseEntity<?> response = authController.register(registerRequest);
        
        // Verify response status code
        assertEquals(400, response.getStatusCode().value());
        
        // Verify response body
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("Username already exists", authResponse.getToken());
        
        // Verify that userService.registerUser was called with correct user data
        verify(userService).registerUser(argThat(user -> 
            user.getUsername().equals("existingUser") &&
            user.getEmail().equals("test@gmail.com") &&
            user.getRole() == UserRole.USER
        ));
    }

    @Test
    void testLoginDuplicateEmailFailure() {
        // Mock behavior
        RegisterRequest registerRequest = new RegisterRequest("newUser", "existing@email.com", "test123", UserRole.USER);

        when(userService.registerUser(any(User.class)))
            .thenThrow(new IllegalArgumentException("Email already exists"));

        ResponseEntity<?> response = authController.register(registerRequest);
        
        // Verify response status code
        assertEquals(400, response.getStatusCode().value());
        
        // Verify response body
        AuthResponse authResponse = (AuthResponse) response.getBody();
        assertNotNull(authResponse);
        assertEquals("Email already exists", authResponse.getToken());
        
        // Verify that userService.registerUser was called with correct user data
        verify(userService).registerUser(argThat(user -> 
            user.getUsername().equals("newUser") &&
            user.getEmail().equals("existing@email.com") &&
            user.getRole() == UserRole.USER
        ));
    }
}
