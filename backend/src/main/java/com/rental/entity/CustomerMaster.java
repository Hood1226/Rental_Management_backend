package com.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "customer_master", schema = "rental_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class CustomerMaster {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer customerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private AppUser user;
    
    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;
    
    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;
    
    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;
    
    @Column(name = "email", length = 150)
    private String email;
    
    @Column(name = "address", columnDefinition = "TEXT")
    private String address;
    
    @Column(name = "id_proof_type", length = 50)
    private String idProofType;
    
    @Column(name = "id_proof_no", length = 50)
    private String idProofNo;
    
    @Column(name = "id_proof_img", columnDefinition = "TEXT")
    private String idProofImg;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public AppUser getUser() {
		return user;
	}

	public void setUser(AppUser user) {
		this.user = user;
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

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(String updatedBy) {
		this.updatedBy = updatedBy;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}
}


