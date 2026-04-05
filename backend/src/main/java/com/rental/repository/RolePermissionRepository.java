package com.rental.repository;

import com.rental.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, Integer> {
    List<RolePermission> findByRole_RoleId(Integer roleId);
    Optional<RolePermission> findByRole_RoleIdAndModule_ModuleId(Integer roleId, Integer moduleId);
}
