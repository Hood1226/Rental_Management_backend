package com.rental.repository;

import com.rental.entity.RentPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface RentPriceRepository extends JpaRepository<RentPrice, Integer> {
    @Query("SELECT rp FROM RentPrice rp WHERE rp.variant.variantId = :variantId " +
           "AND (rp.effectiveTo IS NULL OR rp.effectiveTo >= :date) " +
           "AND rp.effectiveFrom <= :date ORDER BY rp.effectiveFrom DESC")
    Optional<RentPrice> findCurrentPrice(@Param("variantId") Integer variantId, @Param("date") LocalDate date);
}


