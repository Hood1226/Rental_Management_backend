package com.rental.repository;

import com.rental.entity.SequenceCounter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface SequenceCounterRepository extends JpaRepository<SequenceCounter, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SequenceCounter s WHERE s.sequenceKey = :sequenceKey")
    Optional<SequenceCounter> findBySequenceKeyForUpdate(@Param("sequenceKey") String sequenceKey);
}
