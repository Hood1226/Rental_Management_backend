package com.rental.repository;

import com.rental.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByIsActiveTrue();
    List<Product> findByIsForRentTrueAndIsActiveTrue();
    List<Product> findByIsForSaleTrueAndIsActiveTrue();
    List<Product> findByCategory(String category);
}


