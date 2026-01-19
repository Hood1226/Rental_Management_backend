package com.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "sale_price", schema = "rental_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class SalePrice {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sale_price_id")
    private Integer salePriceId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    private ProductVariant variant;
    
    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;
    
    @Column(name = "effective_from")
    private LocalDate effectiveFrom;
    
    @Column(name = "effective_to")
    private LocalDate effectiveTo;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

	public Integer getSalePriceId() {
		return salePriceId;
	}

	public void setSalePriceId(Integer salePriceId) {
		this.salePriceId = salePriceId;
	}

	public ProductVariant getVariant() {
		return variant;
	}

	public void setVariant(ProductVariant variant) {
		this.variant = variant;
	}

	public BigDecimal getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(BigDecimal salePrice) {
		this.salePrice = salePrice;
	}

	public LocalDate getEffectiveFrom() {
		return effectiveFrom;
	}

	public void setEffectiveFrom(LocalDate effectiveFrom) {
		this.effectiveFrom = effectiveFrom;
	}

	public LocalDate getEffectiveTo() {
		return effectiveTo;
	}

	public void setEffectiveTo(LocalDate effectiveTo) {
		this.effectiveTo = effectiveTo;
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
}


