package com.fastmd.backend.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fastmd.backend.domain.entity.User;
import com.fastmd.backend.domain.repository.UserRepository;
import com.fastmd.backend.dto.request.AuthRequest;
import com.fastmd.backend.dto.request.MarkdownFileRequest;
import com.fastmd.backend.dto.request.UserRequest;
import com.fastmd.backend.dto.response.AuthResponse;
import com.fastmd.backend.dto.response.UserResponse;
import com.fastmd.backend.exception.ResourceNotFoundException;
import com.fastmd.backend.security.JwtTokenProvider;
import com.fastmd.backend.service.MarkdownFileService;
import com.fastmd.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final String WELCOME_MARKDOWN = """
            ### 🚀 fast.md - Your Speedy Markdown Companion ✨
            fast.md is a quick and easy-to-use markdown editor perfect for your everyday needs, such as:
            - 📝 Jotting down quick notes and ideas
            - 📅 Creating daily planners and to-do lists
            - ✍️ And much more!

            ### ⚙️ How it Works
            Effortless Markdown Formatting:
            - **Click to Generate**: Simply click the buttons at the top of the editor to generate markdown syntax templates. For example, click the "bold" button to get:
            ```
            **{}** (This is the bold syntax).
            ```
            Just add your text between the asterisks!
            - **Select and Drag**: Type your text, select the words you want to format, drag them to the desired button, and voilà! Your text is instantly converted to the selected markdown syntax.

            ### ✨ Future Plans
            - **LocalStorage for Enhanced Privacy**: We're transitioning from PostgreSQL to localStorage. This means you'll enjoy improved privacy and seamless offline functionality!
            - **Expanding Markdown Support**: Get ready for even more markdown syntax options to make your writing experience even better.
            - **Your Ideas are Welcome!** Have a cool idea for fast.md? Head over to our GitHub repository and share it with us: [https://github.com/daFoggo/fast.md](url). Navigate to the "Issues" tab, create a new issue, and tag it with the **"enhancement"** label. Let's make fast.md amazing together! 💡
            """;

    private final MarkdownFileService markdownFileService;
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
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

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

        UserResponse createdUserResponse = userService.createUser(userRequest);

        User newUser = userRepository.findById(createdUserResponse.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MarkdownFileRequest mdRequest = new MarkdownFileRequest();
        mdRequest.setTitle("Welcome to fast.md!");
        mdRequest.setContent(WELCOME_MARKDOWN);

        markdownFileService.createMarkdownFile(mdRequest, newUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUserResponse);
    }
}