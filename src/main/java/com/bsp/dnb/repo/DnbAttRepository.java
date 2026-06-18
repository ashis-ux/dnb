package com.bsp.dnb.repo;

import com.bsp.dnb.entity.DnbAtt;
import com.bsp.dnb.entity.DnbAttId;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DnbAttRepository
        extends JpaRepository<DnbAtt, DnbAttId> {

    List<DnbAtt> findByIdId(Integer id);

    List<DnbAtt> findByIdYymm(Integer yymm);

    List<DnbAtt> findByIdIdAndIdYymm(
            Integer id,
            Integer yymm);
    
    Page<DnbAtt> findByIdYymm(
            Integer yymm,
            Pageable pageable);

    List<DnbAtt> findDistinctByIdYymmIsNotNullOrderByIdYymmDesc();
    
    @Query("""
    	       SELECT DISTINCT a.id.yymm
    	       FROM DnbAtt a
    	       ORDER BY a.id.yymm DESC
    	       """)
    	List<Integer> findDistinctYymm();
    
    @Query("""
    	       SELECT a
    	       FROM DnbAtt a
    	       WHERE a.id.yymm = :yymm
    	       AND a.id.id IN :employeeIds
    	       """)
    	Page<DnbAtt> findAttendanceForAuthorizedEmployees(
    	        @Param("yymm")
    	        Integer yymm,

    	        @Param("employeeIds")
    	        List<Integer> employeeIds,

    	        Pageable pageable);
}