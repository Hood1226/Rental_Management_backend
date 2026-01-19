package com.rental.repository;

import com.rental.entity.SalePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface SalePriceRepository extends JpaRepository<SalePrice, Integer> {
    @Query("SELECT sp FROM SalePrice sp WHERE sp.variant.variantId = :variantId " +
           "AND (sp.effectiveTo IS NULL OR sp.effectiveTo >= :date) " +
           "AND sp.effectiveFrom <= :date ORDER BY sp.effectiveFrom DESC")
    Optional<SalePrice> findCurrentPrice(@Param("variantId") Integer variantId, @Param("date") LocalDate date);
}


