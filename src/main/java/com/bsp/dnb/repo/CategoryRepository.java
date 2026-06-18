package com.bsp.dnb.repo;
 
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.entity.Category;

@Repository
public interface CategoryRepository
        extends JpaRepository<Category, Integer> {
	
	boolean existsByDnbRole_Id(Long roleId);
	
	List<Category> findByDnbRole_Id(Long roleId);
	
	boolean existsByCatgAndDnbRole_Id(
	        Integer catg,
	        Long roleId);
	
	@Query("""
		       SELECT c
		       FROM Category c
		       WHERE c.dnbRole.id = :roleId
		       AND c.year = 1
		       ORDER BY c.catg
		       """)
		List<Category> findCategoriesForDropdown(
		        @Param("roleId") Long roleId);
	
	 @Query(
		        value = """
		                SELECT STIPEND
		                FROM DNB_CATG_MAST
		                WHERE CATG = :catg
		                AND YEAR = :year
		                FETCH FIRST 1 ROWS ONLY
		                """,
		        nativeQuery = true)
		    Integer findStipend(
		            @Param("catg")
		            Integer catg,

		            @Param("year")
		            Integer year);
	 
	 @Query("""
		       SELECT DISTINCT c.catg
		       FROM Category c
		       WHERE c.dnbRole.id = :roleId
		       """)
		List<Integer> findAllowedCategories(
		        @Param("roleId") Long roleId);

}