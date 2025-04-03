package com.moneymap.userservice.controller;

import com.moneymap.userservice.model.User;
import com.moneymap.userservice.model.UserRole;
import com.moneymap.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserProfileSuccess() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password", UserRole.USER);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));

        // Act
        ResponseEntity<?> response = userController.getUserProfile(authentication);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("testuser", responseBody.get("username"));
        assertEquals("test@example.com", responseBody.get("email"));
        assertEquals("USER", responseBody.get("role"));
    }

    @Test
    void testGetUserProfileNotFound() {
        // Arrange
        when(authentication.getName()).thenReturn("nonexistent");
        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        ResponseEntity<?> response = userController.getUserProfile(authentication);

        // Assert
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfileSuccess() throws Exception {
        // Arrange
        User user = new User("testuser", "test@example.com", "oldPassword", UserRole.USER);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.validatePassword(any(User.class), any(String.class))).thenReturn(false);
        when(userService.changePassword(any(User.class), any(String.class))).thenReturn(user);

        Map<String, String> updates = new HashMap<>();
        updates.put("password", "newPassword");

        // Act
        ResponseEntity<?> response = userController.updateProfile(authentication, updates);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Profile updated successfully", responseBody.get("message"));
    }

    @Test
    void testUpdateProfileUserNotFound() throws Exception {
        // Arrange
        when(authentication.getName()).thenReturn("nonexistent");
        when(userService.findByUsername("nonexistent")).thenReturn(Optional.empty());

        Map<String, String> updates = new HashMap<>();
        updates.put("password", "newPassword");

        // Act
        ResponseEntity<?> response = userController.updateProfile(authentication, updates);

        // Assert
        assertEquals(404, response.getStatusCode().value());
    }

    @Test
    void testUpdateProfileWithInvalidPassword() throws Exception {
        // Arrange
        User user = new User("testuser", "test@example.com", "oldPassword", UserRole.USER);
        when(authentication.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(Optional.of(user));
        when(userService.validatePassword(any(User.class), any(String.class))).thenReturn(true);
        when(userService.changePassword(any(User.class), any(String.class)))
            .thenThrow(new Exception("Password must be different than current"));

        Map<String, String> updates = new HashMap<>();
        updates.put("password", "oldPassword");

        // Act
        ResponseEntity<?> response = userController.updateProfile(authentication, updates);

        // Assert
        assertEquals(400, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Password must be different than current", responseBody.get("message"));
    }
} 