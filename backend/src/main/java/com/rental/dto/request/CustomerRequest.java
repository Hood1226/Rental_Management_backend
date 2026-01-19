package com.rental.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequest {
    private Integer userId; // Optional - link to AppUser if exists
    
    @NotBlank(message = "Customer name is required")
    @Size(max = 150, message = "Customer name must not exceed 150 characters")
    private String customerName;
    
    @Size(max = 20, message = "Mobile number must not exceed 20 characters")
    private String mobileNumber;
    
    @Size(max = 20, message = "WhatsApp number must not exceed 20 characters")
    private String whatsappNumber;
    
    @Email(message = "Invalid email format")
    @Size(max = 150, message = "Email must not exceed 150 characters")
    private String email;
    
    private String address;
    
    @Size(max = 50, message = "ID proof type must not exceed 50 characters")
    private String idProofType;
    
    @Size(max = 50, message = "ID proof number must not exceed 50 characters")
    private String idProofNo;
    
    private String idProofImg; // Base64 or URL

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getMobileNumber() {
		return mobileNumber;
	}

	public void setMobileNumber(String mobileNumber) {
		this.mobileNumber = mobileNumber;
	}

	public String getWhatsappNumber() {
		return whatsappNumber;
	}

	public void setWhatsappNumber(String whatsappNumber) {
		this.whatsappNumber = whatsappNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getIdProofType() {
		return idProofType;
	}

	public void setIdProofType(String idProofType) {
		this.idProofType = idProofType;
	}

	public String getIdProofNo() {
		return idProofNo;
	}

	public void setIdProofNo(String idProofNo) {
		this.idProofNo = idProofNo;
	}

	public String getIdProofImg() {
		return idProofImg;
	}

	public void setIdProofImg(String idProofImg) {
		this.idProofImg = idProofImg;
	}
}

