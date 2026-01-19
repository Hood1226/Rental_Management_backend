package com.rental.repository;

import com.rental.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Integer> {
    Optional<AppUser> findByEmail(String email);
    
    Optional<AppUser> findByContactNumber(String contactNumber);
    
    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.role WHERE u.email = :email")
    Optional<AppUser> findByEmailWithRole(@Param("email") String email);
    
    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.role WHERE u.contactNumber = :contactNumber")
    Optional<AppUser> findByContactNumberWithRole(@Param("contactNumber") String contactNumber);
    
    @Query("SELECT u FROM AppUser u LEFT JOIN FETCH u.role WHERE u.email = :emailOrMobile OR u.contactNumber = :emailOrMobile")
    Optional<AppUser> findByEmailOrContactNumberWithRole(@Param("emailOrMobile") String emailOrMobile);
    
    boolean existsByEmail(String email);
    
    boolean existsByContactNumber(String contactNumber);
}


