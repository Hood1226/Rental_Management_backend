package com.rental.repository;

import com.rental.entity.DamageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DamageRecordRepository extends JpaRepository<DamageRecord, Integer> {
    @Query("SELECT dr FROM DamageRecord dr JOIN FETCH dr.transaction WHERE dr.transaction.transactionId = :transactionId")
    List<DamageRecord> findByTransactionTransactionId(@Param("transactionId") Integer transactionId);
    
    @Query("SELECT dr FROM DamageRecord dr JOIN FETCH dr.transaction WHERE dr.transaction.booking.bookingId = :bookingId")
    List<DamageRecord> findByTransactionBookingBookingId(@Param("bookingId") Integer bookingId);
    
    @Query("SELECT dr FROM DamageRecord dr JOIN FETCH dr.transaction WHERE dr.transaction.transactionId IN :transactionIds")
    List<DamageRecord> findByTransactionTransactionIds(@Param("transactionIds") List<Integer> transactionIds);
}

