package com.moneymap.userservice.security;

import com.moneymap.userservice.model.UserRole;
import com.moneymap.userservice.model.User;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970"
})
public class JwtUtilTest {

    @Autowired
    private JwtUtil jwtUtil;

    private User mockUser;

    @BeforeEach
    void setUp() {
        // Mocking a User object
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setEmail("testuser@example.com");
        mockUser.setPassword("testPassword");
        mockUser.setRole(UserRole.USER);
    }

    @Test
    void testGenerateToken() {
        String token = jwtUtil.generateToken(mockUser);

        assertNotNull(token);
        assertTrue(token.startsWith("eyJ")); // JWT tokens usually start with "eyJ"
    }

    @Test
    void testExtractUsername() {
        String token = jwtUtil.generateToken(mockUser);
        String username = jwtUtil.extractUsername(token);

        assertEquals(mockUser.getUsername(), username);
    }

    @Test
    void testExtractClaims() {
        String token = jwtUtil.generateToken(mockUser);
        Claims claims = jwtUtil.extractAllClaims(token);

        assertEquals(mockUser.getUsername(), claims.getSubject());
        assertEquals(mockUser.getEmail(), claims.get("email"));
        assertEquals(mockUser.getRole().toString(), claims.get("role"));
    }

    @Test
    void testIsTokenExpired() {
        String token = jwtUtil.generateToken(mockUser);
        boolean isExpired = jwtUtil.isTokenExpired(token);

        assertFalse(isExpired);
    }

    @Test
    void testValidateToken() {
        String token = jwtUtil.generateToken(mockUser);
        boolean isValid = jwtUtil.validateToken(token);

        assertTrue(isValid);
    }
}
