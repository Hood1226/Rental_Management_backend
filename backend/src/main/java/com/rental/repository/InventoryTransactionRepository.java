package com.rental.repository;

import com.rental.entity.InventoryTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    @Query("SELECT it FROM InventoryTransaction it JOIN FETCH it.variant v JOIN FETCH v.product JOIN FETCH v.size WHERE it.booking.bookingId = :bookingId")
    List<InventoryTransaction> findByBookingBookingId(@Param("bookingId") Integer bookingId);
    
    @Query("SELECT it FROM InventoryTransaction it JOIN FETCH it.variant WHERE it.variant.variantId = :variantId")
    List<InventoryTransaction> findByVariantVariantId(@Param("variantId") Integer variantId);
    
    List<InventoryTransaction> findByTransactionType(String transactionType);
}

