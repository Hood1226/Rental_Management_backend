package com.rental.entity;

import jakarta.persistence.*;

@Entity
@Table(
        name = "sequence_counter",
        schema = "rental_management",
        uniqueConstraints = @UniqueConstraint(name = "uk_sequence_counter_key", columnNames = "sequence_key")
)
public class SequenceCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "counter_id")
    private Integer counterId;

    @Column(name = "sequence_key", nullable = false, length = 100)
    private String sequenceKey;

    @Column(name = "last_number", nullable = false)
    private Integer lastNumber = 0;

    public Integer getCounterId() {
        return counterId;
    }

    public void setCounterId(Integer counterId) {
        this.counterId = counterId;
    }

    public String getSequenceKey() {
        return sequenceKey;
    }

    public void setSequenceKey(String sequenceKey) {
        this.sequenceKey = sequenceKey;
    }

    public Integer getLastNumber() {
        return lastNumber;
    }

    public void setLastNumber(Integer lastNumber) {
        this.lastNumber = lastNumber;
    }
}
