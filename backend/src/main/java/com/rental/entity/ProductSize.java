package com.rental.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_size", schema = "rental_management")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductSize {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Integer sizeId;
    
    @Column(name = "size_code", unique = true, nullable = false, length = 20)
    private String sizeCode;

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
}


