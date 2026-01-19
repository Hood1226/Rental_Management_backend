package com.rental.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name must not exceed 150 characters")
    private String productName;
    
    @Size(max = 100, message = "Category must not exceed 100 characters")
    private String category;
    
    private String description;
    
    private BigDecimal depositAmount;
    
    private Boolean isForSale = false;
    
    private Boolean isForRent = true;
    
    private Boolean isActive = true;
    
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

	public List<ProductVariantRequest> getVariants() {
		return variants;
	}

	public void setVariants(List<ProductVariantRequest> variants) {
		this.variants = variants;
	}

	// Product variants with their prices and inventory
    @Valid
    private List<ProductVariantRequest> variants;
    
    @Data
    public static class ProductVariantRequest {
        @NotNull(message = "Size ID is required")
        private Integer sizeId;
        
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
        private Integer availableQuantity = 0;
        private String availabilityStatus;
        private LocalDate expectedRestoreDate;
        private LocalDate nextAvailabilityDate;
        
		public Integer getSizeId() {
			return sizeId;
		}
		public void setSizeId(Integer sizeId) {
			this.sizeId = sizeId;
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
    }
}
