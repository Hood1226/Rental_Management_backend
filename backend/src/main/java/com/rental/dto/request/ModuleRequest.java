package com.rental.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ModuleRequest {
    @NotBlank(message = "Module name is required")
    private String moduleName;
    
    @NotBlank(message = "Module key is required")
    private String moduleKey;
    
    private String description;

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
}
