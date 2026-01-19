package com.rental.repository;

import com.rental.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    Optional<Inventory> findByVariantVariantId(Integer variantId);
    List<Inventory> findByAvailabilityStatus(String status);
    List<Inventory> findByAvailableQuantityGreaterThan(Integer quantity);
}


