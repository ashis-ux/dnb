package com.bsp.dnb.repo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bsp.dnb.entity.BankCds;
import com.bsp.dnb.entity.BankCdsId;

public interface BankCdsRepository
        extends JpaRepository<BankCds, BankCdsId> {

    @Query("""
            SELECT b.id.bankCode
            FROM BankCds b
            WHERE UPPER(b.bankName) = UPPER(:bankName)
            AND UPPER(b.id.ifsCd) = UPPER(:ifscCode)
            """)
    Optional<Integer> findBankCode(
            @Param("bankName")
            String bankName,

            @Param("ifscCode")
            String ifscCode);
    
    @Query("""
    	       SELECT DISTINCT b.bankName
    	       FROM BankCds b
    	       ORDER BY b.bankName
    	       """)
    	List<String> findAllBankNames();
    
    @Query("""
		       SELECT DISTINCT b.bankName
		       FROM BankCds b
		       WHERE UPPER(TRIM(b.id.ifsCd))
		             = UPPER(TRIM(:ifscCode))
		       ORDER BY b.bankName
		       """)
		List<String> findDistinctBankNamesByIfsc(
		        @Param("ifscCode")
		        String ifscCode);


}