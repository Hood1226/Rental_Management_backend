package com.rental.dto.response;

/**
 * Response DTO for GET /auth/me - current authenticated user.
 * Frontend expects roleId for /roles/{roleId}/permissions.
 */
public class CurrentUserResponse {
    private Integer userId;
    private String userName;
    private String email;
    private Integer roleId;
    private String roleName;

    public CurrentUserResponse() {
    }

    public CurrentUserResponse(Integer userId, String userName, String email, Integer roleId, String roleName) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.roleId = roleId;
        this.roleName = roleName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
