package com.bsp.dnb.serviceImpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.StipendStatusDto;
import com.bsp.dnb.entity.DnbMonthlyUpdateAudit;
import com.bsp.dnb.exception.ApplicationException;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.repo.DnbMonthlyUpdateAuditRepository;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.repo.PayPymtDataRepository;
import com.bsp.dnb.service.DnbRoleService;
import com.bsp.dnb.service.StipendService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StipendServiceImpl
        implements StipendService {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DnbPbillRepository pbillRepository;
    
    
    @Autowired
    private PayPymtDataRepository payPymtDataRepository;
    
    @Autowired
    private DnbMonthlyUpdateAuditRepository dnbMonthlyUpdateAuditRepository;
 

    @Value("${dnb.stipend.procedure-name}")
    private String procedureName;

    @Override
    public String createStipend() {

        log.info("Create stipend request received.");
        
   
        Integer curMonthYYMM =
                Integer.parseInt(
                        YearMonth.now()
                                .format(DateTimeFormatter.ofPattern("yyMM")));

        Integer latestSapMonth =
                payPymtDataRepository.findLatestPaymentMonth();

        log.info("Previous Month (yyMM): {}", curMonthYYMM);
        log.info("Latest SAP Month      : {}", latestSapMonth);

        if (latestSapMonth != null
                && latestSapMonth.equals(curMonthYYMM)) {

            log.warn("Salary already posted into SAP for month {}.", curMonthYYMM);

            throw new BadRequestException(
                    "Salary has already been posted into SAP for "+latestSapMonth);
        }
        
        //check wheather user already update the master
        Integer curMonthYYMM1 =
                Integer.parseInt(
                        YearMonth.now()
                        .minusMonths(1)
                                .format(DateTimeFormatter.ofPattern("yyyyMM")));

        Long yymm = curMonthYYMM1.longValue();

        log.info("Checking audit data for current month YYMM: {}", curMonthYYMM1);

        DnbMonthlyUpdateAudit audit =
                dnbMonthlyUpdateAuditRepository.findByYymm(yymm)
                        .orElseThrow(() -> {
                            log.error("No Update master found for YYMM: {}", curMonthYYMM1);
                            return new BadRequestException(
                                    "Please Update the Master for " + curMonthYYMM1);
                        });

        log.info("Audit data found for YYMM: {} with status: {}",
                curMonthYYMM1, audit.getStatus());
        
        
        
        
        Integer maxYymm =
                pbillRepository.findMaxYymm();

        Integer previousMonth =
                Integer.parseInt(
                        YearMonth.now()
                                .minusMonths(1)
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyyyMM")));

        log.info(
                "Database YYMM : {}, Previous Month : {}",
                maxYymm,
                previousMonth);

        if (previousMonth.equals(maxYymm)) {

            log.info(
                    "Stipend already created for {}",
                    previousMonth);
            
            throw new BadRequestException(
                    "Stipend list already created for this month.");
        }

        executeProcedure();

        log.info(
                "Stipend procedure executed successfully.");

        return "Check stipend list and verify.";
    }

    private void executeProcedure() {

        log.info(
                "Executing procedure : {}",
                procedureName);

        try (Connection connection =
                     dataSource.getConnection();

             CallableStatement callableStatement =
                     connection.prepareCall(
                             "{call " + procedureName + "()}")) {

            callableStatement.execute();

            log.info(
                    "Procedure {} executed successfully.",
                    procedureName);

        } catch (Exception ex) {

            log.error(
                    "Error while executing procedure : {}",
                    procedureName,
                    ex);

            throw new ApplicationException(
                    "Unable to execute stipend procedure.");
        }
    }
    
    @Override
    public StipendStatusDto getStatus() {

        StipendStatusDto dto =
                new StipendStatusDto();

        boolean authorized =
                SecurityUtil.hasRole("MAST")
                || SecurityUtil.hasRole("ROLE_SUPER_ADMIN");
//                || SecurityUtil.hasRole("SU");

        dto.setAuthorized(authorized);
//
        if (!authorized) {

            dto.setPaybillGenerated(false);

            dto.setMessage(
                    "You are not authorised to access this option.");

            return dto;
        }
        
        Integer curMonthYYMM =
                Integer.parseInt(
                        YearMonth.now()
                                .format(DateTimeFormatter.ofPattern("yyMM")));

        Integer latestSapMonth =
                payPymtDataRepository.findLatestPaymentMonth();

        log.info("Previous Month (yyMM): {}", curMonthYYMM);
        log.info("Latest SAP Month      : {}", latestSapMonth);

        if (latestSapMonth != null
                && latestSapMonth.equals(curMonthYYMM)) {

            log.warn("Salary already posted into SAP for month {}.", curMonthYYMM);

            dto.setMessage(
                    "Stipend already posted into SAP for this month.");
            dto.setPaybillGenerated(true);
            
            return dto;
        }

        Integer maxYymm =
                pbillRepository.findMaxYymm();

        Integer previousMonth =
                Integer.parseInt(
                        YearMonth.now()
                                .minusMonths(1)
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyyyMM")));

        dto.setPreviousMonth(previousMonth);

        dto.setPaybillGenerated(
                previousMonth.equals(maxYymm));

        if (previousMonth.equals(maxYymm)) {
        	log.warn("Stipend already generated for this month.", previousMonth);
            dto.setMessage(
                    "Stipend already generated for this month.");

        } else {

            dto.setMessage(
                    "Ready to generate stipend.");
        }

        return dto;
    }
    
}