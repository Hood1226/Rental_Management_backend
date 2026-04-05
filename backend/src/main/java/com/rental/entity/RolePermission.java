package com.rental.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "role_permission", schema = "rental_management")
@EntityListeners(AuditingEntityListener.class)
public class RolePermission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "permission_id")
    private Integer permissionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;
    
    @Column(name = "can_create")
    private Boolean canCreate = false;
    
    @Column(name = "can_read")
    private Boolean canRead = false;
    
    @Column(name = "can_update")
    private Boolean canUpdate = false;
    
    @Column(name = "can_delete")
    private Boolean canDelete = false;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public RolePermission() {
    }
    
    public RolePermission(Integer permissionId, Role role, Module module, Boolean canCreate, Boolean canRead, Boolean canUpdate, Boolean canDelete, LocalDateTime createdAt) {
        this.permissionId = permissionId;
        this.role = role;
        this.module = module;
        this.canCreate = canCreate;
        this.canRead = canRead;
        this.canUpdate = canUpdate;
        this.canDelete = canDelete;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Integer getPermissionId() {
        return permissionId;
    }
    
    public void setPermissionId(Integer permissionId) {
        this.permissionId = permissionId;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public Module getModule() {
        return module;
    }
    
    public void setModule(Module module) {
        this.module = module;
    }
    
    public Boolean getCanCreate() {
        return canCreate;
    }
    
    public void setCanCreate(Boolean canCreate) {
        this.canCreate = canCreate;
    }
    
    public Boolean getCanRead() {
        return canRead;
    }
    
    public void setCanRead(Boolean canRead) {
        this.canRead = canRead;
    }
    
    public Boolean getCanUpdate() {
        return canUpdate;
    }
    
    public void setCanUpdate(Boolean canUpdate) {
        this.canUpdate = canUpdate;
    }
    
    public Boolean getCanDelete() {
        return canDelete;
    }
    
    public void setCanDelete(Boolean canDelete) {
        this.canDelete = canDelete;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
