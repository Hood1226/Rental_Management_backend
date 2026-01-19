package com.rental.controller;

import com.rental.dto.request.LoginRequest;
import com.rental.dto.request.RegisterRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.LoginResponse;
import com.rental.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"}, allowCredentials = "true")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication APIs - Login, Register, and Logout")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
	@Autowired
    private AuthService authService;
    
    @Value("${spring.security.jwt.expiration:86400000}")
    private Long jwtExpiration;
    
    private static final String TOKEN_COOKIE_NAME = "authToken";
    
    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with email or mobile number and password. Returns JWT token in HTTP-only cookie.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Login successful - JWT token set in cookie"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Invalid credentials"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        logger.info("Login attempt for email/mobile: {}", request.getEmailOrMobile());
        
        try {
            LoginResponse response = authService.login(request);
            
            // Create HTTP-only cookie with proper settings
            ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, response.getToken())
                    .path("/")  // Root path - cookie will be sent for all requests
                    .httpOnly(true)
                    .secure(false)  // Set to true in production with HTTPS
                    .sameSite("Lax")
                    .maxAge(jwtExpiration / 1000)  // Convert milliseconds to seconds
                    .build();
            
            // Remove token from response body for security
            response.setToken(null);
            
            logger.info("Login successful for user: {} (ID: {}), cookie set with path /", 
                    response.getEmail(), response.getUserId());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(ApiResponse.success("Login successful", response));
        } catch (Exception e) {
            logger.error("Login failed for email/mobile: {} - Error: {}", request.getEmailOrMobile(), e.getMessage(), e);
            throw e;
        }
    }
    
    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account. Returns JWT token in HTTP-only cookie.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Registration successful - JWT token set in cookie"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid input data or email already exists")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        logger.info("Registration attempt for email: {}, username: {}", request.getEmail(), request.getUserName());
        
        try {
            LoginResponse response = authService.register(request);
            
            // Create HTTP-only cookie with proper settings
            ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, response.getToken())
                    .path("/")  // Root path - cookie will be sent for all requests
                    .httpOnly(true)
                    .secure(false)  // Set to true in production with HTTPS
                    .sameSite("Lax")
                    .maxAge(jwtExpiration / 1000)  // Convert milliseconds to seconds
                    .build();
            
            // Remove token from response body for security
            response.setToken(null);
            
            logger.info("Registration successful for user: {} (ID: {}), role: {}", 
                    response.getEmail(), response.getUserId(), response.getRoleName());
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(ApiResponse.success("Registration successful", response));
        } catch (Exception e) {
            logger.error("Registration failed for email: {} - Error: {}", request.getEmail(), e.getMessage(), e);
            throw e;
        }
    }
    
    @PostMapping("/logout")
    @Operation(summary = "User logout", description = "Logout user by clearing the authentication cookie")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Logout successful")
    })
    public ResponseEntity<ApiResponse<String>> logout() {
        logger.info("Logout request received");
        
        // Clear the auth token cookie by setting maxAge to 0
        ResponseCookie cookie = ResponseCookie.from(TOKEN_COOKIE_NAME, "")
                .path("/")  // Root path - must match the path used when setting the cookie
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)  // Delete cookie
                .build();
        
        logger.info("Logout successful - auth token cookie cleared");
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("Logout successful", null));
    }
}


