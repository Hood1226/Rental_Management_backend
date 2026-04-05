package com.rental.dto.response;

public class ModuleResponse {
    private Integer moduleId;
    private String moduleName;
    private String moduleKey;
    private String description;

    public ModuleResponse() {
    }

    public ModuleResponse(Integer moduleId, String moduleName, String moduleKey, String description) {
        this.moduleId = moduleId;
        this.moduleName = moduleName;
        this.moduleKey = moduleKey;
        this.description = description;
    }

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
}
