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
public class BookingResponse {
    private Integer bookingId;
    private Integer customerId;
    private String customerName;
    private String bookingType;
    private LocalDateTime bookingDate;
    private String status;
    private BigDecimal totalAmount;
    private String createdBy;
    private LocalDateTime createdAt;
    private String updatedBy;
    private LocalDateTime updatedAt;
    private List<BookingItemResponse> items;
    private List<InventoryTransactionResponse> transactions;
    
    public Integer getBookingId() {
		return bookingId;
	}

	public void setBookingId(Integer bookingId) {
		this.bookingId = bookingId;
	}

	public Integer getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Integer customerId) {
		this.customerId = customerId;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getBookingType() {
		return bookingType;
	}

	public void setBookingType(String bookingType) {
		this.bookingType = bookingType;
	}

	public LocalDateTime getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDateTime bookingDate) {
		this.bookingDate = bookingDate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public BigDecimal getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(BigDecimal totalAmount) {
		this.totalAmount = totalAmount;
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

	public List<BookingItemResponse> getItems() {
		return items;
	}

	public void setItems(List<BookingItemResponse> items) {
		this.items = items;
	}

	public List<InventoryTransactionResponse> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<InventoryTransactionResponse> transactions) {
		this.transactions = transactions;
	}

	@Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookingItemResponse {
        private Integer bookingItemId;
        private Integer variantId;
        private Integer productId;
        private String productName;
        private String sizeCode;
        private Integer quantity;
        private BigDecimal unitPrice;
        private LocalDate rentalStart;
        private LocalDate rentalEnd;
        private BigDecimal subtotal;
		public Integer getBookingItemId() {
			return bookingItemId;
		}
		public void setBookingItemId(Integer bookingItemId) {
			this.bookingItemId = bookingItemId;
		}
		public Integer getVariantId() {
			return variantId;
		}
		public void setVariantId(Integer variantId) {
			this.variantId = variantId;
		}
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
		public String getSizeCode() {
			return sizeCode;
		}
		public void setSizeCode(String sizeCode) {
			this.sizeCode = sizeCode;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public BigDecimal getUnitPrice() {
			return unitPrice;
		}
		public void setUnitPrice(BigDecimal unitPrice) {
			this.unitPrice = unitPrice;
		}
		public LocalDate getRentalStart() {
			return rentalStart;
		}
		public void setRentalStart(LocalDate rentalStart) {
			this.rentalStart = rentalStart;
		}
		public LocalDate getRentalEnd() {
			return rentalEnd;
		}
		public void setRentalEnd(LocalDate rentalEnd) {
			this.rentalEnd = rentalEnd;
		}
		public BigDecimal getSubtotal() {
			return subtotal;
		}
		public void setSubtotal(BigDecimal subtotal) {
			this.subtotal = subtotal;
		}
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryTransactionResponse {
        private Integer transactionId;
        private Integer variantId;
        private Integer productId;
        private String productName;
        private String sizeCode;
        private String transactionType; // RENT_OUT, RETURN, SALE, DAMAGE
        private Integer quantity;
        private LocalDateTime transactionDate;
        private LocalDate expectedReturnDate;
        private LocalDate actualReturnDate;
        private String status;
        private String notes;
        private LocalDateTime createdAt;
        private List<DamageRecordResponse> damageRecords;
		public Integer getTransactionId() {
			return transactionId;
		}
		public void setTransactionId(Integer transactionId) {
			this.transactionId = transactionId;
		}
		public Integer getVariantId() {
			return variantId;
		}
		public void setVariantId(Integer variantId) {
			this.variantId = variantId;
		}
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
		public String getSizeCode() {
			return sizeCode;
		}
		public void setSizeCode(String sizeCode) {
			this.sizeCode = sizeCode;
		}
		public String getTransactionType() {
			return transactionType;
		}
		public void setTransactionType(String transactionType) {
			this.transactionType = transactionType;
		}
		public Integer getQuantity() {
			return quantity;
		}
		public void setQuantity(Integer quantity) {
			this.quantity = quantity;
		}
		public LocalDateTime getTransactionDate() {
			return transactionDate;
		}
		public void setTransactionDate(LocalDateTime transactionDate) {
			this.transactionDate = transactionDate;
		}
		public LocalDate getExpectedReturnDate() {
			return expectedReturnDate;
		}
		public void setExpectedReturnDate(LocalDate expectedReturnDate) {
			this.expectedReturnDate = expectedReturnDate;
		}
		public LocalDate getActualReturnDate() {
			return actualReturnDate;
		}
		public void setActualReturnDate(LocalDate actualReturnDate) {
			this.actualReturnDate = actualReturnDate;
		}
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getNotes() {
			return notes;
		}
		public void setNotes(String notes) {
			this.notes = notes;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
		public List<DamageRecordResponse> getDamageRecords() {
			return damageRecords;
		}
		public void setDamageRecords(List<DamageRecordResponse> damageRecords) {
			this.damageRecords = damageRecords;
		}
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DamageRecordResponse {
        private Integer damageId;
        private Integer transactionId;
        private String description;
        private BigDecimal repairCost;
        private LocalDateTime createdAt;
		public Integer getDamageId() {
			return damageId;
		}
		public void setDamageId(Integer damageId) {
			this.damageId = damageId;
		}
		public Integer getTransactionId() {
			return transactionId;
		}
		public void setTransactionId(Integer transactionId) {
			this.transactionId = transactionId;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public BigDecimal getRepairCost() {
			return repairCost;
		}
		public void setRepairCost(BigDecimal repairCost) {
			this.repairCost = repairCost;
		}
		public LocalDateTime getCreatedAt() {
			return createdAt;
		}
		public void setCreatedAt(LocalDateTime createdAt) {
			this.createdAt = createdAt;
		}
    }
}
