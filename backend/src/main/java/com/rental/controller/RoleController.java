package com.rental.controller;

import com.rental.dto.request.RoleRequest;
import com.rental.dto.response.ApiResponse;
import com.rental.dto.response.RolePermissionResponse;
import com.rental.dto.response.RoleResponse;
import com.rental.entity.Role;
import com.rental.repository.RoleRepository;
import com.rental.service.RolePermissionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3002"}, allowCredentials = "true")
@RequiredArgsConstructor
public class RoleController {

    private final RoleRepository roleRepository;
    private final RolePermissionService rolePermissionService;

    @Autowired
    public RoleController(RoleRepository roleRepository, RolePermissionService rolePermissionService) {
        this.roleRepository = roleRepository;
        this.rolePermissionService = rolePermissionService;
    }

    @GetMapping
    public ResponseEntity<List<RoleResponse>> getAllRoles() {
        List<RoleResponse> list = roleRepository.findAll().stream()
                .map(this::toRoleResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(list);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoleResponse>> createRole(@Valid @RequestBody RoleRequest request) {
        if (roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Role name already exists"));
        }
        Role role = new Role();
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role = roleRepository.save(role);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(toRoleResponse(role)));
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<ApiResponse<RoleResponse>> updateRole(@PathVariable Integer roleId, @Valid @RequestBody RoleRequest request) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        if (!role.getRoleName().equals(request.getRoleName())
                && roleRepository.findByRoleName(request.getRoleName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("Role name already exists"));
        }
        role.setRoleName(request.getRoleName());
        role.setDescription(request.getDescription());
        role = roleRepository.save(role);
        return ResponseEntity.ok(ApiResponse.success(toRoleResponse(role)));
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<ApiResponse<Void>> deleteRole(@PathVariable Integer roleId) {
        if (!roleRepository.existsById(roleId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error("Role not found"));
        }
        roleRepository.deleteById(roleId);
        return ResponseEntity.ok(ApiResponse.success("Role deleted successfully", null));
    }

    @GetMapping("/{roleId}/permissions")
    public ResponseEntity<List<RolePermissionResponse>> getPermissionsByRole(@PathVariable Integer roleId) {
        return ResponseEntity.ok(rolePermissionService.getPermissionsByRoleId(roleId));
    }

    @PutMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponse<Void>> updatePermission(@PathVariable Integer roleId, @RequestBody RolePermissionResponse permissionDTO) {
        rolePermissionService.updatePermission(roleId, permissionDTO);
        return ResponseEntity.ok(ApiResponse.success("Permission updated successfully", null));
    }

    private RoleResponse toRoleResponse(Role r) {
        return new RoleResponse(r.getRoleId(), r.getRoleName(), r.getDescription());
    }
}
