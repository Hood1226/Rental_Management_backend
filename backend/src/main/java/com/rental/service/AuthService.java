package com.rental.service;

import com.rental.dto.request.LoginRequest;
import com.rental.dto.request.RegisterRequest;
import com.rental.dto.response.LoginResponse;
import com.rental.entity.AppUser;
import com.rental.entity.Role;
import com.rental.exception.AuthenticationException;
import com.rental.repository.AppUserRepository;
import com.rental.repository.RoleRepository;
import com.rental.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
	
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    
	@Autowired
    private AppUserRepository appUserRepository;
	
	@Autowired
    private RoleRepository roleRepository;
	
	@Autowired
    private PasswordEncoder passwordEncoder;
	
	@Autowired
    private JwtUtil jwtUtil;
    
    private static final String DEFAULT_ROLE = "USER";
    
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        logger.debug("Starting user registration for email: {}", request.getEmail());
        
        // Check if user already exists
        if (appUserRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: User with email {} already exists", request.getEmail());
            throw new AuthenticationException("User with email " + request.getEmail() + " already exists");
        }
        
        // Get or create default role
        String roleName = request.getRoleName() != null && !request.getRoleName().isEmpty() 
                ? request.getRoleName() 
                : DEFAULT_ROLE;
        
        logger.debug("Looking up role: {}", roleName);
        Role role = roleRepository.findByRoleName(roleName)
                .orElseGet(() -> {
                    logger.info("Role {} not found, creating new role", roleName);
                    Role newRole = new Role();
                    newRole.setRoleName(roleName);
                    newRole.setDescription("Default user role");
                    return roleRepository.save(newRole);
                });
        
        // Create new user
        AppUser user = new AppUser();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setContactNumber(request.getContactNumber());
        user.setRole(role);
        user.setStatus(true);
        
        AppUser savedUser = appUserRepository.save(user);
        logger.info("User created successfully: {} (ID: {})", savedUser.getEmail(), savedUser.getUserId());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(
                savedUser.getEmail(),
                savedUser.getUserId(),
                savedUser.getRole().getRoleName()
        );
        logger.debug("JWT token generated for user: {}", savedUser.getEmail());
        
        return new LoginResponse(
                token,
                "Bearer",
                savedUser.getUserId(),
                savedUser.getUserName(),
                savedUser.getEmail(),
                savedUser.getRole().getRoleName()
        );
    }
    
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String emailOrMobile = request.getEmailOrMobile();
        logger.debug("Attempting login for: {}", emailOrMobile);
        
        // Find user by email or mobile number with role eagerly fetched
        AppUser user = appUserRepository.findByEmailOrContactNumberWithRole(emailOrMobile)
                .orElseThrow(() -> {
                    logger.warn("Login failed: User not found with email/mobile: {}", emailOrMobile);
                    return new AuthenticationException("Invalid email/mobile number or password");
                });
        
        // Check if user is active
        if (!user.getStatus()) {
            logger.warn("Login failed: User account is deactivated for: {}", emailOrMobile);
            throw new AuthenticationException("User account is deactivated");
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            logger.warn("Login failed: Invalid password for: {}", emailOrMobile);
            throw new AuthenticationException("Invalid email/mobile number or password");
        }
        
        logger.debug("Password verified successfully for user: {} (Email: {})", 
                emailOrMobile, user.getEmail());
        
        // Generate JWT token
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getUserId(),
                user.getRole().getRoleName()
        );
        logger.debug("JWT token generated for user: {}", user.getEmail());
        
        logger.info("Login successful for user: {} (ID: {}), role: {}", 
                user.getEmail(), user.getUserId(), user.getRole().getRoleName());
        
        return new LoginResponse(
                token,
                "Bearer",
                user.getUserId(),
                user.getUserName(),
                user.getEmail(),
                user.getRole().getRoleName()
        );
    }
}

