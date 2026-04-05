package com.rental.dto.request;

import com.rental.validation.ValidPassword;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.Length;

/**
 * Request DTO for create/update user.
 * For update, password is optional (omit or leave blank to keep existing).
 */
public class UserRequest {

    @NotBlank(message = "Username is required")
    @Length(max = 100)
    private String userName;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    @Length(max = 150)
    private String email;

    /**
     * Mobile number (optional): 10 digits, optional leading + and country code (e.g. +91 or 91).
     */
    @Pattern(
        regexp = "^$|^(\\+?[0-9]{1,4})?[0-9]{10}$",
        message = "Invalid mobile number. Use 10 digits, optionally with country code (e.g. 9876543210 or +919876543210)"
    )
    @Length(max = 20)
    private String contactNumber;

    /**
     * Required for create. For update, leave blank to keep existing password.
     * When provided: min 8 characters, at least one uppercase, one digit, one special character.
     */
    @ValidPassword
    @Length(max = 100)
    private String password;

    private Integer roleId;
    private Boolean status = true;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
