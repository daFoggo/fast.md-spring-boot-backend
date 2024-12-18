package com.fastmd.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastmd.backend.domain.entity.User;
import com.fastmd.backend.domain.repository.UserRepository;
import com.fastmd.backend.dto.request.AuthRequest;
import com.fastmd.backend.dto.request.UserRequest;
import com.fastmd.backend.dto.response.AuthResponse;
import com.fastmd.backend.dto.response.UserResponse;
import com.fastmd.backend.exception.AuthException;
import com.fastmd.backend.security.JwtTokenProvider;
import com.fastmd.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
    
            // Generate JWT token
            String jwt = tokenProvider.generateToken(authentication);
    
            // Get user details from repository
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    
            // Create response object
            UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    
            AuthResponse response = AuthResponse.builder()
                .token(jwt)
                .user(Optional.of(userResponse))
                .build();
    
            return ResponseEntity.ok(response);
    
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody AuthRequest request) {
        UserRequest userRequest = new UserRequest();
        userRequest.setUsername(request.getUsername());
        userRequest.setPassword(request.getPassword());

        UserResponse user = userService.createUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
}
