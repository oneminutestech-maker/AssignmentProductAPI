package com.example.product.controller.login;

import com.example.product.config.JwtUtils;
import com.example.product.dto.AuthRequestDto;
import com.example.product.dto.AuthResponseDto;
import com.example.product.model.User;
import com.example.product.repository.UserRepository;
import com.example.product.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication API")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    @PostMapping("/register")
    @Operation(summary = "Register new user")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody AuthRequestDto dto) {
        if (userRepository.findByUsername(dto.getUsername()).isPresent()) {
            return createResponse(false, HttpStatus.BAD_REQUEST, "Username already exists", null);
        }

        userRepository.save(User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(User.Role.USER)
                .build());

        return createResponse(true, HttpStatus.CREATED, "User registered successfully", null);
    }

    @PostMapping("/login")
    @Operation(summary = "User login")
    public ResponseEntity<ApiResponse<AuthResponseDto>> login(@Valid @RequestBody AuthRequestDto dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));

        String token = jwtUtils.generateToken(dto.getUsername());
        Date expirationTime = new Date(System.currentTimeMillis() + 30 * 60 * 1000);

        return createResponse(true, HttpStatus.OK, "Login successful",
                new AuthResponseDto(token, expirationTime));
    }

    private <T> ResponseEntity<ApiResponse<T>> createResponse(boolean success, HttpStatus status,
                                                              String message, T data) {
        return new ResponseEntity<>(
                new ApiResponse<>(success, status.value(), message, data),
                status);
    }
}