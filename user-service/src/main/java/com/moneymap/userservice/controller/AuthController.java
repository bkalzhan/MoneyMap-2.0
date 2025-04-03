package com.moneymap.userservice.controller;

import com.moneymap.userservice.model.AuthRequest;
import com.moneymap.userservice.model.AuthResponse;
import com.moneymap.userservice.model.RegisterRequest;
import com.moneymap.userservice.model.User;
import com.moneymap.userservice.security.JwtUtil;
import com.moneymap.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            // Create new user from request
            User newUser = new User();
            newUser.setUsername(registerRequest.getUsername());
            newUser.setEmail(registerRequest.getEmail());
            newUser.setPassword(registerRequest.getPassword());
            newUser.setRole(registerRequest.getRole());

            // Register the user
            User registeredUser = userService.registerUser(newUser);

            // Generate token for the new user
            String token = jwtUtil.generateToken(registeredUser);

            // Create response
            AuthResponse response = new AuthResponse(
                token,
                registeredUser.getUsername(),
                registeredUser.getEmail(),
                registeredUser.getRole().toString()
            );

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new AuthResponse(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        Optional<User> userOptional = userService.findByUsername(authRequest.getUsername());
        
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userService.validatePassword(user, authRequest.getPassword())) {
                String token = jwtUtil.generateToken(user);
                
                AuthResponse response = new AuthResponse(
                    token,
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().toString()
                );
                
                return ResponseEntity.ok(response);
            }
        }
        
        return ResponseEntity.status(401)
            .body(new AuthResponse("Invalid username or password"));
    }
} 