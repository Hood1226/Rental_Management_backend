package com.rental.repository;

import com.rental.entity.ProductSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer> {
    Optional<ProductSize> findBySizeCode(String sizeCode);
}


