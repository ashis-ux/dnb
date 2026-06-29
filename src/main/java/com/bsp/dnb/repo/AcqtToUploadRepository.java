package com.bsp.dnb.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.dnb.entity.AcqtToUpload;

@Repository
public interface AcqtToUploadRepository
        extends JpaRepository<AcqtToUpload, Integer> {

    boolean existsByPerno(
            Integer perno);

    long count();

    @Modifying
    @Transactional
    @Query(
            value = """
                    DELETE FROM ACQT_TOUPLOAD
                    """,
            nativeQuery = true)
    void deleteAllData();

    @Modifying
    @Transactional
    @Query(
            value = """
                    INSERT INTO ACQT_TOUPLOAD
                    (
                        PERNO,
                        AMT,
                        PAN
                    )

                    SELECT

                        A.ID,

                        A.NPAY,

                        B.PAN

                    FROM DNBPBILL A,

                         DNBMAST B,

                         BANK_CDS C

                    WHERE

                        A.ID = B.ID

                    AND

                        A.YYMM =
                        (
                            SELECT MAX(YYMM)
                            FROM DNBPBILL
                        )

                    AND

                        B.BANK_CD = C.BANK_CODE

                    AND

                        NVL(A.NPAY,0) <> 0

                    AND

                        B.CATG < 7
                    """,
            nativeQuery = true)
    int insertIntoAcqtToUpload();
}