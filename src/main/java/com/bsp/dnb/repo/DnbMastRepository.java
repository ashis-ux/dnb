package com.bsp.dnb.repo;
import org.springframework.data.domain.Pageable;


import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

 
import com.bsp.dnb.entity.DnbMast;

@Repository
public interface DnbMastRepository
        extends JpaRepository<DnbMast, Integer> {
	
	@Query("SELECT COALESCE(MAX(d.id), 0) FROM DnbMast d")
    Integer findMaxId();
 
//	@Query("SELECT d FROM DnbMast d WHERE d.empStatus = 0 AND ( d.dos IS NULL OR d.dos > :previousMonthStart)")
//		List<DnbMast> findEligibleForAttendance(
//		        Date previousMonthStart);
	
	@Query(value = "SELECT dm.* FROM DNBMAST dm JOIN DNB_CATG_MAST cm ON dm.CATG = cm.CATG WHERE cm.ROLE = :roleId AND dm.EMP_STATUS = 0 AND ( dm.DOS IS NULL OR dm.DOS > :previousMonthStart) ORDER BY dm.NAME", nativeQuery = true)
	List<DnbMast> findEligibleForAttendance(
	        @Param("roleId") Long roleId,
	        @Param("previousMonthStart") Date previousMonthStart);
	boolean existsByPanIgnoreCase(String pan);
	boolean existsByPanIgnoreCaseAndIdNot(String pan, Integer id);
	
	Page<DnbMast> findByYymm(
	        Integer yymm,
	        Pageable pageable);

	List<DnbMast> findByYymm(
	        Integer yymm);
	
	Page<DnbMast> findByYymmAndCatgIn(
	        Integer yymm,
	        List<Integer> catgs,
	        Pageable pageable);
	
	@Query("""
		       SELECT DISTINCT d.yymm
		       FROM DnbMast d
		       WHERE d.yymm IS NOT NULL
		       ORDER BY d.yymm DESC
		       """)
		List<Integer> findDistinctYymm();
}