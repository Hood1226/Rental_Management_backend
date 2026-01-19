package com.rental.repository;

import com.rental.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Integer> {
    @Query("SELECT v FROM ProductVariant v JOIN FETCH v.size WHERE v.product.productId = :productId")
    List<ProductVariant> findByProductProductId(@Param("productId") Integer productId);
    
    @Query("SELECT v FROM ProductVariant v JOIN FETCH v.size JOIN FETCH v.product WHERE v.product.productId = :productId AND v.size.sizeId = :sizeId")
    Optional<ProductVariant> findByProductProductIdAndSizeSizeId(@Param("productId") Integer productId, @Param("sizeId") Integer sizeId);
}


