package com.moneymap.userservice.controller;

import com.moneymap.userservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AdminControllerTest {

    @InjectMocks
    private AdminController adminController;

    @Mock
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetDashboard() {
        // Act
        ResponseEntity<?> response = adminController.getDashboard();

        // Assert
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("Welcome to Admin Dashboard", responseBody.get("message"));
    }

    @Test
    void testDeleteUser() {
        // Act
        ResponseEntity<?> response = adminController.deleteUser(1L);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User deleted successfully", responseBody.get("message"));
    }

    @Test
    void testUpdateUserRole() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("role", "ADMIN");

        // Act
        ResponseEntity<?> response = adminController.updateUserRole(1L, request);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        @SuppressWarnings("unchecked")
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody);
        assertEquals("User role updated successfully", responseBody.get("message"));
    }
} 