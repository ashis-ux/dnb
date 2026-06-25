package com.bsp.dnb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.dto.PayslipSearchDto;
import com.bsp.dnb.entity.DnbAtt;
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
	  
	   
	  Optional<DnbPbill> findByIdYymmAndIdId(
		        Integer yymm,
		        Integer id);
	  
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
	  
	  @Query("""
			  SELECT new com.bsp.dnb.dto.PayslipSearchDto(

			          p.id.id,

			          m.name,

			          m.catgDesc,

			          p.id.yymm

			  )

			  FROM DnbPbill p

			  JOIN DnbMast m
			  ON p.id.id = m.id

			  WHERE

			  (:id IS NULL OR m.id = :id)

			  AND

			  (:yymm IS NULL OR p.id.yymm = :yymm)

			  AND

			  m.catg IN :categories

			  ORDER BY

			  p.id.yymm DESC,

			  m.name ASC
			  """)
			  Page<PayslipSearchDto> searchPayslips(

			          @Param("id")
			          Integer id,

			          @Param("yymm")
			          Integer yymm,

			          @Param("categories")
			          List<Integer> categories,

			          Pageable pageable);

	    

}