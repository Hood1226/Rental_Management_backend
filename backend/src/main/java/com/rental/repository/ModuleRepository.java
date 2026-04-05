package com.rental.repository;

import com.rental.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {
    Optional<Module> findByModuleKey(String moduleKey);
}
