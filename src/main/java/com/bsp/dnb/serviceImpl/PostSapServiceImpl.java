package com.bsp.dnb.serviceImpl;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.PostSapStatusDto;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.repo.AcqtToUploadRepository;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.repo.PayPymtDataRepository;
import com.bsp.dnb.service.PostSapService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import java.sql.CallableStatement;
import java.sql.Connection;

@Service
@Slf4j
public class PostSapServiceImpl
        implements PostSapService {

    @Autowired
    private DnbPbillRepository pbillRepository;

    @Autowired
    private PayPymtDataRepository payPymtDataRepository;

    @Autowired
    private AcqtToUploadRepository acqtRepository;

    @Autowired
    private DataSource dataSource;

    @Value("${dnb.sap.procedure-name}")
    private String procedureName;

    @Override
    public PostSapStatusDto getStatus() {

        log.info("Fetching Post Into SAP status.");

        PostSapStatusDto dto =
                new PostSapStatusDto();

        boolean authorized =
                SecurityUtil.hasRole("FIN")
                || SecurityUtil.hasRole("SU")
                || SecurityUtil.hasRole("ROLE_SUPER_ADMIN");

        dto.setAuthorized(
                authorized);

        if (!authorized) {

            log.warn(
                    "User is not authorised for Post Into SAP.");

            dto.setMessage(
                    "You are not authorised to access this option.");

            return dto;
        }

        Integer previousMonth =
                Integer.parseInt(
                        YearMonth.now()
                                .minusMonths(1)
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyyyMM")));

        dto.setPreviousMonth(
                previousMonth);

        Integer maxYymm =
                pbillRepository.findMaxYymm();

        boolean paybillGenerated =
                previousMonth.equals(
                        maxYymm);

        dto.setPaybillGenerated(
                paybillGenerated);

        Integer paymentMonth =
                payPymtDataRepository
                        .findLatestPaymentMonth();

        Integer curYymm =
                Integer.parseInt(
                        YearMonth.now()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyMM")));

        boolean sapPaymentAvailable =
        		curYymm.equals(
                        paymentMonth);

        dto.setSapPaymentAvailable(
                sapPaymentAvailable);
        
        if (sapPaymentAvailable) {

            dto.setMessage(
                    "Stipend already posted into SAP for this month.");

            log.info(
                    "Stipend already posted into SAP for this month.");

            return dto;
        }

        if (!paybillGenerated) {

            dto.setMessage(
                    "Create stipend first, then continue.");

            log.info(
                    "Paybill not generated for previous month.");

            return dto;
        }

        dto.setMessage(
                "Ready to push into SAP.");

        log.info(
                "Post Into SAP status prepared successfully.");

        return dto;
    }
    
    @Override
    @Transactional
    public String processPayment() {

        log.info("Received request to process payment into SAP.");

        boolean authorized =
                SecurityUtil.hasRole("FIN")
                || SecurityUtil.hasRole("SU");
//                || SecurityUtil.hasRole("ROLE_SUPER_ADMIN");

        if (!authorized) {

            log.warn(
                    "User is not authorised to process payment into SAP.");

            throw new BadRequestException(
                    "You are not authorised to access this option.");
        }

        Integer previousMonth =
                Integer.parseInt(
                        YearMonth.now()
                                .minusMonths(1)
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyyyMM")));

        Integer curYymm =
                Integer.parseInt(
                        YearMonth.now()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyMM")));

        log.info(
                "Previous Month (YYYYMM) : {}, Previous Month (YYMM) : {}",
                previousMonth,
                curYymm);

        /*
         * Validate Stipend Generated
         */
        Integer maxYymm =
                pbillRepository.findMaxYymm();

        if (maxYymm == null
                || !previousMonth.equals(
                        maxYymm)) {

            log.warn(
                    "Stipend not generated for previous month.");

            throw new BadRequestException(
                    "Create stipend first, then continue.");
        }

        /*
         * Validate SAP Payment Data
         */
        Integer paymentMonth =
                payPymtDataRepository
                        .findLatestPaymentMonth();

        if (paymentMonth == null
                || curYymm.equals(
                        paymentMonth)) {

            log.warn(
                    "Stipend already posted into SAP for this month.");

            throw new BadRequestException(
                    "Stipend already posted into SAP for this month.");
        }

        /*
         * Prevent duplicate upload
         */
        if (acqtRepository.count() > 0) {

            log.info(
                    "ACQT_TOUPLOAD already contains data. Clearing old records.");

            acqtRepository.deleteAllData();
        }

        /*
         * Insert Upload Data
         */
        log.info(
                "Inserting records into ACQT_TOUPLOAD.");

        int records =
                acqtRepository.insertIntoAcqtToUpload();

        log.info(
                "{} record(s) inserted into ACQT_TOUPLOAD.",
                records);

        if (records == 0) {

            throw new BadRequestException(
                    "No eligible records found for SAP upload.");
        }

        /*
         * Execute Oracle Procedure
         */
        executeProcedure();

        log.info(
                "Post Into SAP completed successfully.");

        return "Salary has been pushed into SAP successfully.";
    }
    
    private void executeProcedure() {

        log.info(
                "Executing Oracle Procedure : {}",
                procedureName);

        try (Connection connection =
                        dataSource.getConnection();

                CallableStatement callableStatement =
                        connection.prepareCall(
                                "{call "
                                        + procedureName
                                        + "}")) {

            callableStatement.execute();

            log.info(
                    "Oracle Procedure {} executed successfully.",
                    procedureName);

        } catch (Exception ex) {

            log.error(
                    "Error while executing Oracle Procedure : {}",
                    procedureName,
                    ex);

            throw new BadRequestException(
                    "Unable to process payment into SAP.");
        }
    }
}