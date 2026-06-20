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
	
	@Query(value = """
		       SELECT c.*
		       FROM DNB_CATG_MAST c
		       JOIN DNB_ROLE_CATEGORY rc
		            ON c.CATG = rc.CATG
		       WHERE rc.ROLE_ID = :roleId
		       AND c.YEAR = 1
		       ORDER BY c.CATG
		       """,
		       nativeQuery = true)
		List<Category> findCategoriesForDropdown(
		        @Param("roleId") Long roleId);
	
	@Query(value = """
			SELECT c.*
			FROM DNB_CATG_MAST c
			JOIN DNB_ROLE_CATEGORY rc
			     ON c.CATG = rc.CATG
			WHERE rc.ROLE_ID = :roleId
			ORDER BY c.CATG,c.YEAR
			""",
			nativeQuery = true)
			List<Category> findByRole(
			        @Param("roleId")
			        Long roleId);
	
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
	 
	 @Query(value = """
		       SELECT DISTINCT CATG
		       FROM DNB_ROLE_CATEGORY
		       WHERE ROLE_ID = :roleId
		       ORDER BY CATG
		       """,
		       nativeQuery = true)
		List<Integer> findAllowedCategories(
		        @Param("roleId") Long roleId);

}