package com.bsp.dnb.repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.entity.DnbPbill;
import com.bsp.dnb.entity.DnbPbillId;

@Repository
public interface DnbPbillRepository
        extends JpaRepository<DnbPbill, DnbPbillId> {

	 List<DnbPbill> findByIdYymm(Integer yymm);

	 boolean existsByIdYymm(Integer yymm);

}