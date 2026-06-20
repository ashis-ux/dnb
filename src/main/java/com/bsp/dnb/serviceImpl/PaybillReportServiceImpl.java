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

import com.bsp.dnb.dto.PaybillReportDto;
import com.bsp.dnb.entity.DnbMast;
import com.bsp.dnb.entity.DnbPbill;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.repo.DnbPbillRepository;
import com.bsp.dnb.repo.RoleCategoryRepository;
import com.bsp.dnb.service.PaybillReportService;

@Service
public class PaybillReportServiceImpl
        implements PaybillReportService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    PaybillReportServiceImpl.class);

    @Autowired
    private DnbPbillRepository dnbPbillRepository;

    @Autowired
    private DnbMastRepository dnbMastRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private RoleCategoryRepository roleCategoryRepository;

    @Value("${app.logged-in-role}")
    private Long loggedInRole;

    @Override
    public Page<PaybillReportDto>
    getPaybillReport(
            Integer yymm,
            int page,
            int size) {

        log.info(
                "Fetching paybill report for YYMM : {}",
                yymm);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id.id"));

        List<Integer> allowedCategories =
                categoryRepository
                        .findAllowedCategories(
                                loggedInRole);

        log.info(
                "Allowed categories : {}",
                allowedCategories);

        Page<DnbPbill> records =
                dnbPbillRepository
                        .findByYymmAndCategories(
                                yymm,
                                allowedCategories,
                                pageable);

        List<PaybillReportDto> dtoList =
                new ArrayList<>();

        for (DnbPbill paybill :
                records.getContent()) {

            DnbMast employee =
                    dnbMastRepository
                            .findById(
                                    paybill
                                            .getId()
                                            .getId())
                            .orElse(null);

            if (employee == null) {
                continue;
            }

            dtoList.add(
                    entityToDto(
                            paybill,
                            employee));
        }

        log.info(
                "Total records after filtering : {}",
                dtoList.size());

        return new PageImpl<>(
                dtoList,
                pageable,
                records.getTotalElements());
    }
    
    
    private PaybillReportDto
    entityToDto(
            DnbPbill paybill,
            DnbMast employee) {

        PaybillReportDto dto =
                new PaybillReportDto();

        dto.setId(
                employee.getId());

        dto.setName(
                employee.getName());

        dto.setCatg(
                employee.getCatg());

        dto.setCatgDesc(
                employee.getCatgDesc());

        dto.setYymm(
                paybill.getId()
                        .getYymm());

        dto.setStipend(
                paybill.getStipend());

        dto.setAdj(
                paybill.getAdj());

        dto.setItaxrec(
                paybill.getItaxrec());

        dto.setCessrec(
                paybill.getCessrec());

        dto.setCessaddl(
                paybill.getCessaddl());

        dto.setGpay(
                paybill.getGpay());

        dto.setNpay(
                paybill.getNpay());

        dto.setHigherTaxInd(
                paybill.getHigherTaxInd());

        return dto;
    }

    @Override
    public ByteArrayInputStream
    exportPaybillReport(
            Integer yymm) {

        log.info(
                "Exporting paybill report for YYMM : {}",
                yymm);

        List<DnbPbill> paybills =
                dnbPbillRepository
                        .findByIdYymm(
                                yymm);

        try (
                Workbook workbook =
                        new XSSFWorkbook();
                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()) {

            Sheet sheet =
                    workbook.createSheet(
                            "Paybill Report");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("ID");

            header.createCell(1)
                    .setCellValue("NAME");

            header.createCell(2)
                    .setCellValue("CATG");

            header.createCell(3)
                    .setCellValue("CATG DESC");

            header.createCell(4)
                    .setCellValue("YYMM");

            header.createCell(5)
                    .setCellValue("STIPEND");

            header.createCell(6)
                    .setCellValue("ADJ");

            header.createCell(7)
                    .setCellValue("ITAX");

            header.createCell(8)
                    .setCellValue("CESS");

            header.createCell(9)
                    .setCellValue("CESS ADDL");

            header.createCell(10)
                    .setCellValue("GPAY");

            header.createCell(11)
                    .setCellValue("NPAY");

            header.createCell(12)
                    .setCellValue("HIGHER TAX");

            int rowNum = 1;

            for (DnbPbill paybill :
                paybills) {

            DnbMast employee =
                    dnbMastRepository
                            .findById(
                                    paybill
                                            .getId()
                                            .getId())
                            .orElse(null);

            if (employee == null) {

                continue;
            }

            boolean hasAccess =
            		roleCategoryRepository
                            .existsByRoleIdAndCatg(loggedInRole,
                                    employee.getCatg());

            if (!hasAccess) {

                continue;
            }

            PaybillReportDto dto =
                    entityToDto(
                            paybill,
                            employee);

            Row row =
                    sheet.createRow(
                            rowNum++);

            row.createCell(0)
                    .setCellValue(dto.getId());

            row.createCell(1)
                    .setCellValue(dto.getName());

            row.createCell(2)
                    .setCellValue(dto.getCatg());

            row.createCell(3)
                    .setCellValue(dto.getCatgDesc());

            row.createCell(4)
                    .setCellValue(dto.getYymm());

            row.createCell(5)
                    .setCellValue(dto.getStipend());

            row.createCell(6)
                    .setCellValue(dto.getAdj());

            row.createCell(7)
                    .setCellValue(dto.getItaxrec());

            row.createCell(8)
                    .setCellValue(dto.getCessrec());

            row.createCell(9)
                    .setCellValue(dto.getCessaddl());

            row.createCell(10)
                    .setCellValue(dto.getGpay());

            row.createCell(11)
                    .setCellValue(dto.getNpay());

            row.createCell(12)
                    .setCellValue(dto.getHigherTaxInd());
        }

            workbook.write(out);

            return new ByteArrayInputStream(
                    out.toByteArray());

        } catch (Exception ex) {

            log.error(
                    "Error exporting paybill report",
                    ex);

            throw new RuntimeException(
                    "Error generating excel",
                    ex);
        }
    }
}