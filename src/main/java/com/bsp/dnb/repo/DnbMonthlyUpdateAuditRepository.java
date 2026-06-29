package com.bsp.dnb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.entity.DnbMonthlyUpdateAudit;

@Repository
public interface DnbMonthlyUpdateAuditRepository extends JpaRepository<DnbMonthlyUpdateAudit, Long> {

    // Find by primary key (YYMM)
    Optional<DnbMonthlyUpdateAudit> findByYymm(Long yymm);

   
}