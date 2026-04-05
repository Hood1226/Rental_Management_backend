package com.rental.repository;

import com.rental.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BranchRepository extends JpaRepository<Branch, Integer> {
    List<Branch> findByShopShopIdAndIsActiveTrue(Integer shopId);
}
