package com.rental.repository;

import com.rental.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Integer> {
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.customer WHERE b.customer.customerId = :customerId")
    List<Booking> findByCustomerCustomerId(@Param("customerId") Integer customerId);
    
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.customer WHERE b.bookingType = :bookingType")
    List<Booking> findByBookingType(@Param("bookingType") String bookingType);
    
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.customer WHERE b.status = :status")
    List<Booking> findByStatus(@Param("status") String status);
    
    @Query("SELECT b FROM Booking b JOIN FETCH b.customer WHERE b.bookingId = :id")
    Optional<Booking> findByIdWithCustomer(@Param("id") Integer id);
    
    @Query("SELECT DISTINCT b FROM Booking b JOIN FETCH b.customer")
    List<Booking> findAllWithCustomer();
}


