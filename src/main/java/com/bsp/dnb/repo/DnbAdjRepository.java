package com.bsp.dnb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
    
    Page<DnbAdj> findByIdYymm(
            Integer yymm,
            Pageable pageable);
    
   
    
    @Query("""
            SELECT a
            FROM DnbAdj a
            WHERE a.id.yymm = :yymm
            AND a.id.id IN :employeeIds
            """)
     Page<DnbAdj> findAuthorizedAdjustments(
             @Param("yymm")
             Integer yymm,
             @Param("employeeIds")
             List<Integer> employeeIds,
             Pageable pageable);

     @Query("""
            SELECT DISTINCT a.id.yymm
            FROM DnbAdj a
            ORDER BY a.id.yymm DESC
            """)
     List<Integer> findDistinctYymm();
}