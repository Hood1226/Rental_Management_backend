package com.rental.repository;

import com.rental.entity.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, Integer> {
    @Query("SELECT bi FROM BookingItem bi JOIN FETCH bi.variant v JOIN FETCH v.product JOIN FETCH v.size WHERE bi.booking.bookingId = :bookingId")
    List<BookingItem> findByBookingBookingId(@Param("bookingId") Integer bookingId);
}

