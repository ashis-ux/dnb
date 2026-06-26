package com.bsp.dnb.serviceImpl;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bsp.dnb.dto.ResetPayStatusDto;
import com.bsp.dnb.exception.BadRequestException;
import com.bsp.dnb.exception.ResourceNotFoundException;
import com.bsp.dnb.repo.DnbCumRepository;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.service.ResetPayService;

import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@Transactional
public class ResetPayServiceImpl
        implements ResetPayService {

    @Autowired
    private DnbPbillRepository pbillRepository;

    @Autowired
    private DnbCumRepository dnbCumRepository;

    @Override
    public String resetPayData() {

        log.info("Reset Pay Data request received.");

        boolean authorized =
                SecurityUtil.hasRole("MAST")
                || SecurityUtil.hasRole("SU")
                || SecurityUtil.hasRole("ROLE_SUPER_ADMIN");

        if (!authorized) {

            log.warn("Unauthorized user attempted to reset pay data.");

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

        log.info(
                "Previous Month YYMM : {}",
                previousMonth);

        boolean paybillExists =
                pbillRepository.existsByIdYymm(
                        previousMonth);

        if (!paybillExists) {

            log.warn(
                    "No paybill found for YYMM : {}",
                    previousMonth);

            throw new ResourceNotFoundException(
                    "No stipend data found for previous month.");
        }

        log.info(
                "Deleting paybill data for YYMM : {}",
                previousMonth);

        pbillRepository.deleteByYymm(
                previousMonth);

        log.info(
                "Deleting DNBCUM data.");

        dnbCumRepository.deleteAllData();

        log.info(
                "Restoring DNBCUM from DNBCUM_PREYM.");

        dnbCumRepository.copyPreviousMonthData();

        log.info(
                "Reset Pay Data completed successfully.");

        return "Stipend Data deleted. Create Stipend Again.";
    }
    
    @Override
    public ResetPayStatusDto getStatus() {

        log.info("Fetching Reset Pay Data status.");

        ResetPayStatusDto dto =
                new ResetPayStatusDto();

        Integer previousMonth =
                Integer.parseInt(
                        YearMonth.now()
                                .minusMonths(1)
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "yyyyMM")));
        dto.setPreviousMonth(
                previousMonth);
        log.info(
                "Previous Month YYMM : {}",
                previousMonth);
        boolean authorized =
                SecurityUtil.hasRole("MAST")
                || SecurityUtil.hasRole("SU")
                || SecurityUtil.hasRole("ROLE_SUPER_ADMIN");

        dto.setAuthorized(
                authorized);

        log.info(
                "User Authorized : {}",
                authorized);

        if (!authorized) {

            log.warn(
                    "Unauthorized user attempted to access Reset Pay Data screen.");

            dto.setMessage(
                    "You are not authorised to access this option.");

            return dto;
        }

        boolean paybillExists =
                pbillRepository.existsByIdYymm(
                        previousMonth);

        dto.setPaybillExists(
                paybillExists);

        log.info(
                "Paybill exists for YYMM {} : {}",
                previousMonth,
                paybillExists);

        if (paybillExists) {

            dto.setMessage(
                    "Paybill available for reset.");

            log.info(
                    "Reset button will be enabled.");

        } else {

            dto.setMessage(
                    "No stipend generated for previous month.");

            log.info(
                    "Reset button will be disabled as no paybill exists.");
        }

        log.info(
                "Reset Pay Data status prepared successfully.");

        return dto;
    }
}