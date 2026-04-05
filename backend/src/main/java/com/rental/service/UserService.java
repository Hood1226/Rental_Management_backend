package com.rental.service;

import com.rental.dto.request.UserRequest;
import com.rental.dto.response.UserResponse;
import com.rental.entity.AppUser;
import com.rental.entity.Role;
import com.rental.repository.AppUserRepository;
import com.rental.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

    @Autowired
    private AppUserRepository appUserRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse createUser(UserRequest request) {
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password is required for new user");
        }
        if (appUserRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }
        if (request.getContactNumber() != null && !request.getContactNumber().isBlank()
                && appUserRepository.existsByContactNumber(normalizeMobile(request.getContactNumber()))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this mobile number already exists");
        }

        AppUser user = new AppUser();
        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setContactNumber(request.getContactNumber() != null ? normalizeMobile(request.getContactNumber()) : null);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setStatus(request.getStatus() != null ? request.getStatus() : true);

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
            user.setRole(role);
        }

        user = appUserRepository.save(user);
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Integer userId, UserRequest request) {
        AppUser user = appUserRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.getEmail().equalsIgnoreCase(request.getEmail().trim())
                && appUserRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this email already exists");
        }
        String newMobile = request.getContactNumber() != null ? normalizeMobile(request.getContactNumber()) : null;
        if (newMobile != null && !newMobile.equals(user.getContactNumber())
                && appUserRepository.existsByContactNumber(newMobile)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User with this mobile number already exists");
        }

        user.setUserName(request.getUserName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setContactNumber(newMobile);
        user.setStatus(request.getStatus() != null ? request.getStatus() : true);

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        if (request.getRoleId() != null) {
            Role role = roleRepository.findById(request.getRoleId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found"));
            user.setRole(role);
        } else if (request.getRoleId() == null && user.getRole() != null) {
            user.setRole(null);
        }

        user = appUserRepository.save(user);
        return toUserResponse(user);
    }

    private String normalizeMobile(String contactNumber) {
        if (contactNumber == null) return null;
        return contactNumber.replaceAll("\\s+", "").trim();
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
