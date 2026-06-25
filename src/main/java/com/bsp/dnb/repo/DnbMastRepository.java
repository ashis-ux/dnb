package com.bsp.dnb.repo;
import org.springframework.data.domain.Pageable;


import java.util.Date;
import java.util.List;
import java.util.Optional;

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
	
	boolean existsByMobileNo(String mobileNo);

	boolean existsByEmailIdIgnoreCase(String emailId);
	
	Optional<DnbMast> findByPanIgnoreCase(
	        String pan);
	
	boolean existsByMobileNoAndIdNot(
	        String mobileNo,
	        Integer id);

	boolean existsByEmailIdIgnoreCaseAndIdNot(
	        String emailId,
	        Integer id);
	
	@Query(value = """
	        SELECT DISTINCT dm.*
	        FROM DNBMAST dm
	        JOIN DNB_ROLE_CATEGORY rc
	             ON dm.CATG = rc.CATG
	        WHERE rc.ROLE_ID = :roleId
	        AND dm.EMP_STATUS = 0
	        AND (
	            dm.DOS IS NULL
	            OR dm.DOS > :previousMonthStart
	        )
	        AND dm.DOJ < :previousMonthEnd
	        ORDER BY dm.NAME, dm.ID
	        """,
	        nativeQuery = true)
	List<DnbMast> findEligibleForAttendance(
	        @Param("roleId") Long roleId,
	        @Param("previousMonthStart") Date previousMonthStart,
	        @Param("previousMonthEnd") Date previousMonthEnd);
	
	
	boolean existsByPanIgnoreCase(String pan);
	boolean existsByPanIgnoreCaseAndIdNot(String pan, Integer id);
	
	Page<DnbMast> findByYymm(
	        Integer yymm,
	        Pageable pageable);

	List<DnbMast> findByYymm(
	        Integer yymm);
	
	List<DnbMast> findByCatgIn(
	        List<Integer> catgs);
	
	Page<DnbMast> findByYymmAndCatgIn(
	        Integer yymm,
	        List<Integer> catgs,
	        Pageable pageable);
	
	Optional<DnbMast> findById(Integer id);
	
	@Query("""
		       SELECT DISTINCT d.yymm
		       FROM DnbMast d
		       WHERE d.yymm IS NOT NULL
		       ORDER BY d.yymm DESC
		       """)
		List<Integer> findDistinctYymm();
}