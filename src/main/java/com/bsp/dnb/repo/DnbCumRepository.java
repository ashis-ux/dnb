package com.bsp.dnb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.dnb.entity.DnbAtt;
import com.bsp.dnb.entity.DnbCum;
import com.bsp.dnb.entity.DnbCumId;

 

@Repository
public interface DnbCumRepository
        extends JpaRepository<DnbCum, DnbCumId> {
	
	List<DnbCum> findByIdIdAndIdYymm(
            Integer id,
            Integer yymm);
	
	@Query("""
		    select d
		    from DnbCum d
		    where d.id.id = :id
		      and d.id.yymm = :yymm
		""")
		List<DnbCum> findAllByIdAndYymm(Integer id, Integer yymm);
	
	@Query(
		    value = """
		        SELECT *
		        FROM dnbcum
		        WHERE id = :id
		          AND yymm = :yymm
		        FETCH FIRST 1 ROW ONLY
		        """,
		    nativeQuery = true
		)
		DnbCum findFirstByIdAndYymm(
		        @Param("id") Integer id,
		        @Param("yymm") Integer yymm);
	
	
	
	@Modifying
	@Transactional
	@Query(
	        value =
	        "DELETE FROM DNBCUM",
	        nativeQuery = true)
	void deleteAllData();

	@Modifying
	@Transactional
	@Query(
	        value =
	        """
	        INSERT INTO DNBCUM
	        SELECT *
	        FROM DNBCUM_PREYM
	        """,
	        nativeQuery = true)
	void copyPreviousMonthData();
}
