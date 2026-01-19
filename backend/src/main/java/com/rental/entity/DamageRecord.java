package com.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "damage_record", schema = "rental_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class DamageRecord {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "damage_id")
    private Integer damageId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private InventoryTransaction transaction;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "repair_cost", precision = 10, scale = 2)
    private BigDecimal repairCost;
    
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

	public Integer getDamageId() {
		return damageId;
	}

	public void setDamageId(Integer damageId) {
		this.damageId = damageId;
	}

	public InventoryTransaction getTransaction() {
		return transaction;
	}

	public void setTransaction(InventoryTransaction transaction) {
		this.transaction = transaction;
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


