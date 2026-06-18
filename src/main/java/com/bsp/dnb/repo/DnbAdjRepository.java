package com.bsp.dnb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.entity.DnbAdj;
import com.bsp.dnb.entity.DnbAdjId;

@Repository
public interface DnbAdjRepository
        extends JpaRepository<DnbAdj, DnbAdjId> {

    List<DnbAdj> findByIdYymm(
            Integer yymm);

    boolean existsByIdYymm(
            Integer yymm);
    
}