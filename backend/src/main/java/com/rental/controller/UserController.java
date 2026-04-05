package com.rental.controller;

import com.rental.dto.request.UserRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.UserResponse;
import com.rental.entity.AppUser;
import com.rental.repository.AppUserRepository;
import com.rental.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3002"}, allowCredentials = "true")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> list = appUserRepository.findAllWithRole().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Integer userId) {
        return appUserRepository.findById(userId)
                .map(this::toUserResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
        UserResponse created = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("User created successfully", created));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(@PathVariable Integer userId, @Valid @RequestBody UserRequest request) {
        UserResponse updated = userService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success("User updated successfully", updated));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        if (appUserRepository.existsById(userId)) {
            appUserRepository.deleteById(userId);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    private UserResponse toUserResponse(AppUser u) {
        Integer roleId = u.getRole() != null ? u.getRole().getRoleId() : null;
        String roleName = u.getRole() != null ? u.getRole().getRoleName() : null;
        return new UserResponse(
                u.getUserId(),
                u.getUserName(),
                u.getEmail(),
                u.getContactNumber(),
                roleId,
                roleName,
                u.getStatus()
        );
    }
}
