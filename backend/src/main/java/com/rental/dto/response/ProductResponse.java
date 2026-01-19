package com.rental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {
    private Integer productId;
    private String productName;
    private String category;
    private String description;
    private BigDecimal depositAmount;
    private Boolean isForSale;
    private Boolean isForRent;
    private Boolean isActive;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<ProductVariantResponse> variants;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductVariantResponse {
        private Integer variantId;
        private Integer sizeId;
        private String sizeCode;
        private BigDecimal purchasePrice;
        
        // Rent price information
        private BigDecimal rentPrice;
        private LocalDate rentEffectiveFrom;
        private LocalDate rentEffectiveTo;
        
        // Sale price information
        private BigDecimal salePrice;
        private LocalDate saleEffectiveFrom;
        private LocalDate saleEffectiveTo;
        
        // Inventory information
        private Integer inventoryId;
        private Integer availableQuantity;
        private String availabilityStatus;
        private LocalDate expectedRestoreDate;
        private LocalDate nextAvailabilityDate;
        
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
		public Integer getVariantId() {
			return variantId;
		}
		public void setVariantId(Integer variantId) {
			this.variantId = variantId;
		}
		public Integer getSizeId() {
			return sizeId;
		}
		public void setSizeId(Integer sizeId) {
			this.sizeId = sizeId;
		}
		public String getSizeCode() {
			return sizeCode;
		}
		public void setSizeCode(String sizeCode) {
			this.sizeCode = sizeCode;
		}
		public BigDecimal getPurchasePrice() {
			return purchasePrice;
		}
		public void setPurchasePrice(BigDecimal purchasePrice) {
			this.purchasePrice = purchasePrice;
		}
		public BigDecimal getRentPrice() {
			return rentPrice;
		}
		public void setRentPrice(BigDecimal rentPrice) {
			this.rentPrice = rentPrice;
		}
		public LocalDate getRentEffectiveFrom() {
			return rentEffectiveFrom;
		}
		public void setRentEffectiveFrom(LocalDate rentEffectiveFrom) {
			this.rentEffectiveFrom = rentEffectiveFrom;
		}
		public LocalDate getRentEffectiveTo() {
			return rentEffectiveTo;
		}
		public void setRentEffectiveTo(LocalDate rentEffectiveTo) {
			this.rentEffectiveTo = rentEffectiveTo;
		}
		public BigDecimal getSalePrice() {
			return salePrice;
		}
		public void setSalePrice(BigDecimal salePrice) {
			this.salePrice = salePrice;
		}
		public LocalDate getSaleEffectiveFrom() {
			return saleEffectiveFrom;
		}
		public void setSaleEffectiveFrom(LocalDate saleEffectiveFrom) {
			this.saleEffectiveFrom = saleEffectiveFrom;
		}
		public LocalDate getSaleEffectiveTo() {
			return saleEffectiveTo;
		}
		public void setSaleEffectiveTo(LocalDate saleEffectiveTo) {
			this.saleEffectiveTo = saleEffectiveTo;
		}
		public Integer getInventoryId() {
			return inventoryId;
		}
		public void setInventoryId(Integer inventoryId) {
			this.inventoryId = inventoryId;
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
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		public LocalDateTime getUpdatedAt() {
			return updatedAt;
		}
		public void setUpdatedAt(LocalDateTime updatedAt) {
			this.updatedAt = updatedAt;
		}
    }
    
    // Getters and setters
    public Integer getProductId() {
        return productId;
    }
    public void setProductId(Integer productId) {
        this.productId = productId;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public BigDecimal getDepositAmount() {
        return depositAmount;
    }
    public void setDepositAmount(BigDecimal depositAmount) {
        this.depositAmount = depositAmount;
    }
    public Boolean getIsForSale() {
        return isForSale;
    }
    public void setIsForSale(Boolean isForSale) {
        this.isForSale = isForSale;
    }
    public Boolean getIsForRent() {
        return isForRent;
    }
    public void setIsForRent(Boolean isForRent) {
        this.isForRent = isForRent;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
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
    public List<ProductVariantResponse> getVariants() {
        return variants;
    }
    public void setVariants(List<ProductVariantResponse> variants) {
        this.variants = variants;
    }
}
