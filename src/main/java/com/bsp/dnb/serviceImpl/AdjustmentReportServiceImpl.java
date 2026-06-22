package com.bsp.dnb.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.AdjustmentReportDto;
import com.bsp.dnb.entity.DnbAdj;
import com.bsp.dnb.entity.DnbMast;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbAdjRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.service.AdjustmentReportService;
import com.bsp.dnb.service.DnbRoleService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AdjustmentReportServiceImpl
        implements AdjustmentReportService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AdjustmentReportServiceImpl.class);

    @Autowired
    private DnbAdjRepository dnbAdjRepository;

    @Autowired
    private DnbMastRepository dnbMastRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
	private DnbRoleService dnbroleservice;

    @Override
    public Page<AdjustmentReportDto>
    getAdjustmentReport(
            Integer yymm,
            int page,
            int size) {

        log.info(
                "Fetching adjustment report for YYMM : {}, Page : {}, Size : {}",
                yymm,
                page,
                size);
        
        Long loggedInRole=dnbroleservice.getRoleId();

        List<Integer> allowedCategories =
                categoryRepository
                        .findAllowedCategories(
                                loggedInRole);

        List<Integer> employeeIds =
                dnbMastRepository
                        .findByCatgIn(
                                allowedCategories)
                        .stream()
                        .map(
                                DnbMast::getId)
                        .toList();

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id.id"));

        Page<DnbAdj> records =
                dnbAdjRepository
                        .findAuthorizedAdjustments(
                                yymm,
                                employeeIds,
                                pageable);

        List<AdjustmentReportDto> dtoList =
                new ArrayList<>();

        for (DnbAdj adjustment :
                records.getContent()) {

            DnbMast employee =
                    dnbMastRepository
                            .findById(
                                    adjustment
                                            .getId()
                                            .getId())
                            .orElse(null);

            if (employee == null) {
                continue;
            }

            dtoList.add(
                    entityToDto(
                            adjustment,
                            employee));
        }

        log.info(
                "Adjustment records returned : {}",
                dtoList.size());

        return new PageImpl<>(
                dtoList,
                pageable,
                records.getTotalElements());
    }

    @Override
    public ByteArrayInputStream
    exportAdjustmentReport(
            Integer yymm) {

        log.info(
                "Exporting adjustment report for YYMM : {}",
                yymm);
        
        Long loggedInRole=dnbroleservice.getRoleId();

        List<Integer> allowedCategories =
                categoryRepository
                        .findAllowedCategories(
                                loggedInRole);

        List<Integer> employeeIds =
                dnbMastRepository
                        .findByCatgIn(
                                allowedCategories)
                        .stream()
                        .map(
                                DnbMast::getId)
                        .toList();

        List<DnbAdj> records =
                dnbAdjRepository
                        .findByIdYymm(
                                yymm)
                        .stream()
                        .filter(
                                a -> employeeIds.contains(
                                        a.getId()
                                                .getId()))
                        .toList();

        try (
                Workbook workbook =
                        new XSSFWorkbook();
                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()) {

            Sheet sheet =
                    workbook.createSheet(
                            "Adjustment Report");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("ID");

            header.createCell(1)
                    .setCellValue("NAME");

            header.createCell(2)
                    .setCellValue("YYMM");

            header.createCell(3)
                    .setCellValue("FOR MONTH");

            header.createCell(4)
                    .setCellValue("DAYS");

            header.createCell(5)
                    .setCellValue("CATEGORY");

            header.createCell(6)
                    .setCellValue("YEAR");

            header.createCell(7)
                    .setCellValue("AMOUNT");

            header.createCell(8)
                    .setCellValue("STOP ADJ");

            header.createCell(9)
                    .setCellValue("PAID");

            int rowNum = 1;

            for (DnbAdj adjustment :
                    records) {

                DnbMast employee =
                        dnbMastRepository
                                .findById(
                                        adjustment
                                                .getId()
                                                .getId())
                                .orElse(null);

                if (employee == null) {
                    continue;
                }

                Row row =
                        sheet.createRow(
                                rowNum++);

                row.createCell(0)
                        .setCellValue(
                                employee.getId());

                row.createCell(1)
                        .setCellValue(
                                employee.getName());

                row.createCell(2)
                        .setCellValue(
                                adjustment
                                        .getId()
                                        .getYymm());

                row.createCell(3)
                        .setCellValue(
                                adjustment
                                        .getId()
                                        .getForym());

                row.createCell(4)
                        .setCellValue(
                                value(
                                        adjustment.getDays()));

                row.createCell(5)
                        .setCellValue(
                                value(
                                        adjustment.getCatg()));

                row.createCell(6)
                        .setCellValue(
                                value(
                                        adjustment.getYr()));

                row.createCell(7)
                        .setCellValue(
                                value(
                                        adjustment.getAmt()));

                row.createCell(8)
                        .setCellValue(
                                value(
                                        adjustment.getStopAdjInd()));

                row.createCell(9)
                        .setCellValue(
                                value(
                                        adjustment.getPaidInd()));
            }

            workbook.write(out);

            log.info(
                    "Adjustment report excel generated successfully");

            return new ByteArrayInputStream(
                    out.toByteArray());

        } catch (Exception ex) {

            log.error(
                    "Error while exporting adjustment report",
                    ex);

            throw new RuntimeException(
                    "Unable to export adjustment report",
                    ex);
        }
    }

    @Override
    public List<Integer>
    getAdjustmentYymm() {

        log.info(
                "Fetching adjustment YYMM dropdown values");

        return dnbAdjRepository
                .findDistinctYymm();
    }

    private AdjustmentReportDto
    entityToDto(
            DnbAdj adjustment,
            DnbMast employee) {

        AdjustmentReportDto dto =
                new AdjustmentReportDto();

        dto.setId(
                employee.getId());

        dto.setName(
                employee.getName());

        dto.setYymm(
                adjustment
                        .getId()
                        .getYymm());

        dto.setForym(
                adjustment
                        .getId()
                        .getForym());

        dto.setDays(
                adjustment.getDays());

        dto.setCatg(
                adjustment.getCatg());

        dto.setYr(
                adjustment.getYr());

        dto.setAmt(
                adjustment.getAmt());

        dto.setStopAdjInd(
                adjustment.getStopAdjInd());

        dto.setPaidInd(
                adjustment.getPaidInd());

        return dto;
    }

    private int value(
            Integer value) {

        return value == null
                ? 0
                : value;
    }
}