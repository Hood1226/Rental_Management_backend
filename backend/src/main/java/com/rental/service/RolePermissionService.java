package com.rental.service;

import com.rental.dto.response.RolePermissionResponse;
import com.rental.entity.Module;
import com.rental.entity.Role;
import com.rental.entity.RolePermission;
import com.rental.repository.ModuleRepository;
import com.rental.repository.RolePermissionRepository;
import com.rental.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RolePermissionService {

    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    
    @Autowired
    private ModuleRepository moduleRepository;
    
    @Autowired
    private RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public List<RolePermissionResponse> getPermissionsByRoleId(Integer roleId) {
        // Get all modules
        List<Module> allModules = moduleRepository.findAll();
        
        // Get existing permissions for the role
        List<RolePermission> existingPermissions = rolePermissionRepository.findByRole_RoleId(roleId);
        Map<Integer, RolePermission> permissionMap = existingPermissions.stream()
                .collect(Collectors.toMap(p -> p.getModule().getModuleId(), Function.identity()));
        
        // Create response list merging modules and permissions
        List<RolePermissionResponse> responseList = new ArrayList<>();
        
        for (Module module : allModules) {
            RolePermissionResponse response = new RolePermissionResponse();
            response.setModuleId(module.getModuleId());
            response.setModuleName(module.getModuleName());
            response.setModuleKey(module.getModuleKey());
            
            if (permissionMap.containsKey(module.getModuleId())) {
                RolePermission perm = permissionMap.get(module.getModuleId());
                response.setCanCreate(perm.getCanCreate());
                response.setCanRead(perm.getCanRead());
                response.setCanUpdate(perm.getCanUpdate());
                response.setCanDelete(perm.getCanDelete());
            } else {
                // Default to false if no permission record exists
                response.setCanCreate(false);
                response.setCanRead(false);
                response.setCanUpdate(false);
                response.setCanDelete(false);
            }
            
            responseList.add(response);
        }
        
        return responseList;
    }

    @Transactional
    public void updatePermission(Integer roleId, RolePermissionResponse permissionDTO) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        
        Module module = moduleRepository.findById(permissionDTO.getModuleId())
                .orElseThrow(() -> new RuntimeException("Module not found"));
        
        RolePermission permission = rolePermissionRepository.findByRole_RoleIdAndModule_ModuleId(roleId, permissionDTO.getModuleId())
                .orElse(new RolePermission());
        
        if (permission.getPermissionId() == null) {
            permission.setRole(role);
            permission.setModule(module);
        }
        
        permission.setCanCreate(permissionDTO.isCanCreate());
        permission.setCanRead(permissionDTO.isCanRead());
        permission.setCanUpdate(permissionDTO.isCanUpdate());
        permission.setCanDelete(permissionDTO.isCanDelete());
        
        rolePermissionRepository.save(permission);
    }
}
