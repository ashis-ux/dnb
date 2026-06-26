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
import com.bsp.dnb.exception.ApplicationException;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.repo.DnbPbillRepository;
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
 

    @Value("${dnb.stipend.procedure-name}")
    private String procedureName;

    @Override
    public String createStipend() {

        log.info("Create stipend request received.");

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

        if (!authorized) {

            dto.setPaybillGenerated(false);

            dto.setMessage(
                    "You are not authorised to access this option.");

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

            dto.setMessage(
                    "Stipend already generated for this month.");

        } else {

            dto.setMessage(
                    "Ready to generate stipend.");
        }

        return dto;
    }
    
}