package com.rental.dto.response;

public class UserResponse {
    private Integer userId;
    private String userName;
    private String email;
    private String contactNumber;
    private Integer roleId;
    private String roleName;
    private Boolean status;

    public UserResponse() {
    }

    public UserResponse(Integer userId, String userName, String email, String contactNumber,
                        Integer roleId, String roleName, Boolean status) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.roleId = roleId;
        this.roleName = roleName;
        this.status = status;
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

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
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

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
