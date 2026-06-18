package com.bsp.dnb.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.entity.DnbPbill;
import com.bsp.dnb.entity.DnbPbillId;

@Repository
public interface DnbPbillRepository
        extends JpaRepository<DnbPbill, DnbPbillId> {

	 List<DnbPbill> findByIdYymm(Integer yymm);

	 boolean existsByIdYymm(Integer yymm);
	 
	  Page<DnbPbill> findByIdYymm(
	            Integer yymm,
	            Pageable pageable);
	  
	  @Query("""
			  SELECT p
			  FROM DnbPbill p,
			       DnbMast m
			  WHERE p.id.id = m.id
			  AND p.id.yymm = :yymm
			  AND m.catg IN :categories
			  """)
			  Page<DnbPbill> findByYymmAndCategories(
			          @Param("yymm")
			          Integer yymm,

			          @Param("categories")
			          List<Integer> categories,

			          Pageable pageable);

	    

}