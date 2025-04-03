package com.moneymap.userservice.service;

import com.moneymap.userservice.model.User;
import com.moneymap.userservice.model.UserRole;
import com.moneymap.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSuccessfulRegistration() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password123", UserRole.USER);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registeredUser = userService.registerUser(user);

        // Assert
        assertNotNull(registeredUser);
        assertEquals("testuser", registeredUser.getUsername());
        assertEquals("test@example.com", registeredUser.getEmail());
        assertEquals("encodedPassword", registeredUser.getPassword());
        assertEquals(UserRole.USER, registeredUser.getRole());
    }

    @Test
    void testRegistrationWithExistingUsername() {
        // Arrange
        User user = new User("existinguser", "test@example.com", "password123", UserRole.USER);
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });
        assertEquals("Username already exists", exception.getMessage());
    }

    @Test
    void testRegistrationWithExistingEmail() {
        // Arrange
        User user = new User("newuser", "existing@example.com", "password123", UserRole.USER);
        when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(user));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.registerUser(user);
        });
        assertEquals("Email already exists", exception.getMessage());
    }

    @Test
    void testRegistrationWithNullRole() {
        // Arrange
        User user = new User("testuser", "test@example.com", "password123", null);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        User registeredUser = userService.registerUser(user);

        // Assert
        assertEquals(UserRole.USER, registeredUser.getRole());
    }

    @Test
    void testPasswordValidation() {
        // Arrange
        User user = new User("testuser", "test@example.com", "encodedPassword", UserRole.USER);
        when(passwordEncoder.matches("correctPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        assertTrue(userService.validatePassword(user, "correctPassword"));
        assertFalse(userService.validatePassword(user, "wrongPassword"));
    }

    @Test
    void testChangePassword() throws Exception {
        // Arrange
        User user = new User("testuser", "test@example.com", "oldPassword", UserRole.USER);
        when(passwordEncoder.matches("newPassword", "oldPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.changePassword(user, "newPassword");

        // Assert
        assertEquals("encodedNewPassword", user.getPassword());
    }

    @Test
    void testChangePasswordWithSamePassword() {
        // Arrange
        User user = new User("testuser", "test@example.com", "samePassword", UserRole.USER);
        when(passwordEncoder.matches("samePassword", "samePassword")).thenReturn(true);

        // Act & Assert
        Exception exception = assertThrows(Exception.class, () -> {
            userService.changePassword(user, "samePassword");
        });
        assertEquals("Password must be different than current", exception.getMessage());
    }
} 