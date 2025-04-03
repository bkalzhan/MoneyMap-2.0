package com.moneymap.userservice.controller;

import com.moneymap.userservice.model.User;
import com.moneymap.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        Optional<User> userOptional = userService.findByUsername(authentication.getName());
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            Map<String, String> response = new HashMap<>();
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("role", user.getRole().toString());
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(Authentication authentication, @RequestBody Map<String, String> updates) throws Exception {
        Optional<User> userOptional = userService.findByUsername(authentication.getName());
        if (userOptional.isPresent()) {
            // Profile update logic
            User user = userOptional.get();
            try {
                userService.changePassword(user, updates.get("password"));
                Map<String, String> response = new HashMap<>();
                response.put("message", "Profile updated successfully");
                return ResponseEntity.ok(response);
            } catch (Exception e) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("message", e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            }
        }
        return ResponseEntity.notFound().build();
    }
}
