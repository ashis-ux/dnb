package com.bsp.dnb.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bsp.dnb.entity.DnbRole;

public interface DnbRoleRepository
        extends JpaRepository<DnbRole, Long> {

    Optional<DnbRole> findByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCase(String name);
    
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
    
    Optional<DnbRole> findByName(
            String name);

}