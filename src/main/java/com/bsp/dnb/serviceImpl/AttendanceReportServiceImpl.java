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

import com.bsp.dnb.dto.AttendanceReportDto;
import com.bsp.dnb.entity.DnbAtt;
import com.bsp.dnb.entity.DnbMast;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbAttRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.service.AttendanceReportService;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class AttendanceReportServiceImpl
        implements AttendanceReportService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AttendanceReportServiceImpl.class);

    @Autowired
    private DnbAttRepository dnbAttRepository;

    @Autowired
    private DnbMastRepository dnbMastRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Value("${app.logged-in-role}")
    private Long loggedInRole;

    @Override
    public Page<AttendanceReportDto>
    getAttendanceReport(
            Integer yymm,
            int page,
            int size) {

        log.info(
                "Fetching attendance report for YYMM : {}",
                yymm);

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

        Page<DnbAtt> records =
                dnbAttRepository
                        .findAttendanceForAuthorizedEmployees(
                                yymm,
                                employeeIds,
                                pageable);

        List<AttendanceReportDto> dtoList =
                new ArrayList<>();

        for (DnbAtt attendance :
                records.getContent()) {

            DnbMast employee =
                    dnbMastRepository
                            .findById(
                                    attendance
                                            .getId()
                                            .getId())
                            .orElse(null);

            if (employee == null) {
                continue;
            }

            dtoList.add(
                    entityToDto(
                            attendance,
                            employee));
        }

        return new PageImpl<>(
                dtoList,
                pageable,
                records.getTotalElements());
    }

    @Override
    public ByteArrayInputStream
    exportAttendanceReport(
            Integer yymm) {

        log.info(
                "Exporting attendance report for YYMM : {}",
                yymm);

        List<DnbAtt> records =
                dnbAttRepository.findByIdYymm(
                        yymm);

        try (
                Workbook workbook =
                        new XSSFWorkbook();
                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()) {

            Sheet sheet =
                    workbook.createSheet(
                            "Attendance Report");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("ID");

            header.createCell(1)
                    .setCellValue("NAME");

            header.createCell(2)
                    .setCellValue("YYMM");

            header.createCell(3)
                    .setCellValue("DUTY");

            header.createCell(4)
                    .setCellValue("AL");

            header.createCell(5)
                    .setCellValue("CL");

            header.createCell(6)
                    .setCellValue("PL");

            header.createCell(7)
                    .setCellValue("ML");

            header.createCell(8)
                    .setCellValue("ABS");

            int rowNum = 1;

            for (DnbAtt attendance :
                    records) {

                DnbMast employee =
                        dnbMastRepository
                                .findById(
                                        attendance
                                                .getId()
                                                .getId())
                                .orElse(null);

                if (employee == null) {
                    continue;
                }

                boolean hasAccess =
                        categoryRepository
                                .existsByCatgAndDnbRole_Id(
                                        employee.getCatg(),
                                        loggedInRole);

                if (!hasAccess) {
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
                                attendance
                                        .getId()
                                        .getYymm());

                row.createCell(3)
                        .setCellValue(
                                value(
                                        attendance.getDuty()));

                row.createCell(4)
                        .setCellValue(
                                value(
                                        attendance.getAl()));

                row.createCell(5)
                        .setCellValue(
                                value(
                                        attendance.getCl()));

                row.createCell(6)
                        .setCellValue(
                                value(
                                        attendance.getPl()));

                row.createCell(7)
                        .setCellValue(
                                value(
                                        attendance.getMl()));

                row.createCell(8)
                        .setCellValue(
                                value(
                                        attendance.getAbs()));
            }

            workbook.write(out);

            log.info(
                    "Attendance Excel generated successfully");

            return new ByteArrayInputStream(
                    out.toByteArray());

        } catch (Exception ex) {

            log.error(
                    "Error while exporting attendance report",
                    ex);

            throw new RuntimeException(
                    "Unable to export attendance report",
                    ex);
        }
    }

    @Override
    public List<Integer>
    getAttendanceYymm() {

        log.info(
                "Fetching attendance YYMM values");

        return dnbAttRepository
                .findDistinctYymm();
    }

    private AttendanceReportDto
    entityToDto(
            DnbAtt attendance,
            DnbMast employee) {

        AttendanceReportDto dto =
                new AttendanceReportDto();

        dto.setId(
                employee.getId());

        dto.setName(
                employee.getName());

        dto.setYymm(
                attendance
                        .getId()
                        .getYymm());

        dto.setDuty(
                attendance.getDuty());

        dto.setAl(
                attendance.getAl());

        dto.setCl(
                attendance.getCl());

        dto.setPl(
                attendance.getPl());

        dto.setMl(
                attendance.getMl());

        dto.setAbs(
                attendance.getAbs());

        return dto;
    }

    private int value(
            Integer val) {
        return val == null
                ? 0
                : val;
    }
}