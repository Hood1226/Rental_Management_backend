package com.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory", schema = "rental_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Inventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_id")
    private Integer inventoryId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false, unique = true)
    private ProductVariant variant;
    
    @Column(name = "available_quantity", nullable = false)
    private Integer availableQuantity = 0;
    
    @Column(name = "availability_status", length = 20)
    private String availabilityStatus;
    
    @Column(name = "expected_restore_date")
    private LocalDate expectedRestoreDate;
    
    @Column(name = "next_availability_date")
    private LocalDate nextAvailabilityDate;
    
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
    
    @PreUpdate
    @PrePersist
    private void updateAvailabilityStatus() {
        // Only auto-update status if it's not already set to a specific status (RENTED, SOLD, etc.)
        if (availableQuantity == null || availableQuantity == 0) {
            // Only set to UNAVAILABLE if status is null or AVAILABLE (not if already RENTED/SOLD)
            if (availabilityStatus == null || 
                "AVAILABLE".equals(availabilityStatus) || 
                "PARTIALLY_RENTED".equals(availabilityStatus) ||
                "PARTIALLY_SOLD".equals(availabilityStatus)) {
                this.availabilityStatus = "UNAVAILABLE";
            }
        } else {
            // If quantity > 0 and status is UNAVAILABLE, set to AVAILABLE
            if ("UNAVAILABLE".equals(availabilityStatus) && availabilityStatus != null) {
                this.availabilityStatus = "AVAILABLE";
            }
        }
    }

	public Integer getInventoryId() {
		return inventoryId;
	}

	public void setInventoryId(Integer inventoryId) {
		this.inventoryId = inventoryId;
	}

	public ProductVariant getVariant() {
		return variant;
	}

	public void setVariant(ProductVariant variant) {
		this.variant = variant;
	}

	public Integer getAvailableQuantity() {
		return availableQuantity;
	}

	public void setAvailableQuantity(Integer availableQuantity) {
		this.availableQuantity = availableQuantity;
	}

	public String getAvailabilityStatus() {
		return availabilityStatus;
	}

	public void setAvailabilityStatus(String availabilityStatus) {
		this.availabilityStatus = availabilityStatus;
	}

	public LocalDate getExpectedRestoreDate() {
		return expectedRestoreDate;
	}

	public void setExpectedRestoreDate(LocalDate expectedRestoreDate) {
		this.expectedRestoreDate = expectedRestoreDate;
	}

	public LocalDate getNextAvailabilityDate() {
		return nextAvailabilityDate;
	}

	public void setNextAvailabilityDate(LocalDate nextAvailabilityDate) {
		this.nextAvailabilityDate = nextAvailabilityDate;
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


