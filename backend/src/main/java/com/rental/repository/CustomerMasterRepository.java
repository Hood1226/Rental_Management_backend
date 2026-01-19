package com.rental.repository;

import com.rental.entity.CustomerMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerMasterRepository extends JpaRepository<CustomerMaster, Integer> {
    Optional<CustomerMaster> findByEmail(String email);
    List<CustomerMaster> findByCustomerNameContainingIgnoreCase(String name);
}


