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
public class BookingRequest {
    @NotNull(message = "Customer ID is required")
    private Integer customerId;
    
    @NotBlank(message = "Booking type is required")
    @Size(max = 20, message = "Booking type must not exceed 20 characters")
    private String bookingType; // RENT / SALE
    
    @Size(max = 20, message = "Status must not exceed 20 characters")
    private String status;
    
    private BigDecimal totalAmount;
    
    @Valid
    private List<BookingItemRequest> items;
    
    // Optional: Create initial inventory transactions when creating booking
    private List<InventoryTransactionRequest> transactions;
    
    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getBookingType() {
        return bookingType;
    }

    public void setBookingType(String bookingType) {
        this.bookingType = bookingType;
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

    public List<BookingItemRequest> getItems() {
        return items;
    }

    public void setItems(List<BookingItemRequest> items) {
        this.items = items;
    }

    public List<InventoryTransactionRequest> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<InventoryTransactionRequest> transactions) {
        this.transactions = transactions;
    }

    @Data
    public static class BookingItemRequest {
        @NotNull(message = "Variant ID is required")
        private Integer variantId;
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        
        private BigDecimal unitPrice;
        
        private LocalDate rentalStart;
        
        private LocalDate rentalEnd;
        
        private BigDecimal subtotal;

        public Integer getVariantId() {
            return variantId;
        }

        public void setVariantId(Integer variantId) {
            this.variantId = variantId;
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
    public static class InventoryTransactionRequest {
        private Integer transactionId; // For updates
        
        @NotNull(message = "Variant ID is required")
        private Integer variantId;
        
        @NotBlank(message = "Transaction type is required")
        private String transactionType; // RENT_OUT, RETURN, SALE, DAMAGE
        
        @NotNull(message = "Quantity is required")
        private Integer quantity;
        
        private LocalDate expectedReturnDate;
        
        private LocalDate actualReturnDate;
        
        private String status;
        
        private String notes;
        
        // Optional: Damage record if transaction type is DAMAGE
        private DamageRecordRequest damageRecord;

		public Integer getVariantId() {
			return variantId;
		}

		public void setVariantId(Integer variantId) {
			this.variantId = variantId;
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

		public DamageRecordRequest getDamageRecord() {
			return damageRecord;
		}

		public void setDamageRecord(DamageRecordRequest damageRecord) {
			this.damageRecord = damageRecord;
		}
		
		public Integer getTransactionId() {
			return transactionId;
		}
		
		public void setTransactionId(Integer transactionId) {
			this.transactionId = transactionId;
		}
    }
    
    @Data
    public static class DamageRecordRequest {
        private Integer damageId; // For updates
        
        private String description;
        private BigDecimal repairCost;
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
		
		public Integer getDamageId() {
			return damageId;
		}
		
		public void setDamageId(Integer damageId) {
			this.damageId = damageId;
		}
    }
}
