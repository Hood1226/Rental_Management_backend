package com.rental.dto.request;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "Email or mobile number is required")
    @JsonProperty("emailOrMobile")
    @JsonAlias({"email", "mobileNumber", "mobile", "contactNumber"}) // Accept multiple field names
    private String emailOrMobile; // Can be either email or mobile number
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Getters and Setters
    public String getEmailOrMobile() {
        return emailOrMobile;
    }
    
    public void setEmailOrMobile(String emailOrMobile) {
        this.emailOrMobile = emailOrMobile;
    }
    
    // Additional setters for backward compatibility
    public void setEmail(String email) {
        this.emailOrMobile = email;
    }
    
    public void setMobileNumber(String mobileNumber) {
        this.emailOrMobile = mobileNumber;
    }
    
    public void setMobile(String mobile) {
        this.emailOrMobile = mobile;
    }
    
    public void setContactNumber(String contactNumber) {
        this.emailOrMobile = contactNumber;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    // Helper method to check if input is email (contains @) or mobile number
    public boolean isEmail() {
        return emailOrMobile != null && emailOrMobile.contains("@");
    }
    
    public boolean isMobileNumber() {
        return emailOrMobile != null && !emailOrMobile.contains("@");
    }
}


