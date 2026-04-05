package com.rental.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "module", schema = "rental_management")
@EntityListeners(AuditingEntityListener.class)
public class Module {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "module_id")
    private Integer moduleId;
    
    @Column(name = "module_name", nullable = false, length = 100)
    private String moduleName;
    
    @Column(name = "module_key", unique = true, nullable = false, length = 50)
    private String moduleKey;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    // Constructors
    public Module() {
    }
    
    public Module(Integer moduleId, String moduleName, String moduleKey, String description, LocalDateTime createdAt) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.moduleKey = moduleKey;
        this.description = description;
        this.createdAt = createdAt;
    }
    
    // Getters and Setters
    public Integer getModuleId() {
        return moduleId;
    }
    
    public void setModuleId(Integer moduleId) {
        this.moduleId = moduleId;
    }
    
    public String getModuleName() {
        return moduleName;
    }
    
    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }
    
    public String getModuleKey() {
        return moduleKey;
    }
    
    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
