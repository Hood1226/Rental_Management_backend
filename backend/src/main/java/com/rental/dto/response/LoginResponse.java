package com.rental.dto.response;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Integer userId;
    private String userName;
    private String email;
    private String roleName;
    
    // Constructors
    public LoginResponse() {
    }
    
    public LoginResponse(String token, String tokenType, Integer userId, String userName, String email, String roleName) {
        this.token = token;
        this.tokenType = tokenType;
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.roleName = roleName;
    }
    
    // Getters and Setters
    public String getToken() {
        return token;
    }
    
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
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
    
    public String getRoleName() {
        return roleName;
    }
    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}

