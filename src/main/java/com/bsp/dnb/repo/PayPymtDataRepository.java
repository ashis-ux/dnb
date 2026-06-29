package com.bsp.dnb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bsp.dnb.entity.PayPymtData;
import com.bsp.dnb.entity.PayPymtDataId;

@Repository
public interface PayPymtDataRepository
        extends JpaRepository<PayPymtData, PayPymtDataId> {

    @Query(value = """
            SELECT MAX(TO_NUMBER(SUBSTR(DOC_NO,3,4)))
            FROM PAY_PYMT_DATA_DEV
            WHERE DOC_NO LIKE '%DN01'
            """,
            nativeQuery = true)
    Integer findLatestPaymentMonth();

}
