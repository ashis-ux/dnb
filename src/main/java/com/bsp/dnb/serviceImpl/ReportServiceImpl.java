package com.bsp.dnb.serviceImpl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import org.springframework.stereotype.Service;

import com.bsp.dnb.dto.DnbMasterReportDto;
import com.bsp.dnb.entity.DnbMast;
import com.bsp.dnb.repo.CategoryRepository;
import com.bsp.dnb.repo.DnbMastRepository;
import com.bsp.dnb.service.ReportService;

import jakarta.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class ReportServiceImpl
        implements ReportService {

    @Autowired
    private DnbMastRepository dnbMastRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    
    private static final Logger log =
            LoggerFactory.getLogger(
                    ReportServiceImpl.class);

    @Value("${app.logged-in-role}")
    private Long loggedInRole;

    @Override
    public Page<DnbMasterReportDto>
    getDnbMasterReport(
            Integer yymm,
            int page,
            int size) {

        log.info(
                "Fetching DNB Master Report for YYMM : {}, Page : {}, Size : {}",
                yymm,
                page,
                size);

        Pageable pageable =
                PageRequest.of(
                        page,
                        size,
                        Sort.by("id"));

        List<Integer> allowedCategories =
                categoryRepository
                        .findAllowedCategories(
                                loggedInRole);

        Page<DnbMast> records =
                dnbMastRepository
                        .findByYymmAndCatgIn(
                                yymm,
                                allowedCategories,
                                pageable);

        List<DnbMasterReportDto> dtoList =
                records.getContent()
                        .stream()
                        .map(this::entityToDto)
                        .toList();

        return new PageImpl<>(
                dtoList,
                pageable,
                records.getTotalElements());
    }

    private DnbMasterReportDto
    entityToDto(
            DnbMast entity) {

        DnbMasterReportDto dto =
                new DnbMasterReportDto();

        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDob(entity.getDob());
        dto.setDoj(entity.getDoj());
        dto.setDos(entity.getDos());
        dto.setEmpStatus(entity.getEmpStatus());
        dto.setStipendRate(entity.getStipendRate());
        dto.setDailyRate(entity.getDailyRate());
        dto.setSexCode(entity.getSexCode());
        dto.setBankCd(entity.getBankCd());
        dto.setBankAcno(entity.getBankAcno());
        dto.setPan(entity.getPan());
        dto.setCatg(entity.getCatg());
        dto.setCatgDesc(entity.getCatgDesc());
        dto.setSpeciality(entity.getSpeciality());
        dto.setTrgDuration(entity.getTrgDuration());

        return dto;
    }

    @Override
    public ByteArrayInputStream
    exportDnbMasterReport(
            Integer yymm) {

        log.info(
                "Exporting DNB Master Report for YYMM : {}",
                yymm);

        List<DnbMast> employees =
                dnbMastRepository.findByYymm(
                        yymm);

        List<DnbMast> authorizedEmployees =
                employees.stream()
                        .filter(emp ->
                                categoryRepository
                                        .existsByCatgAndDnbRole_Id(
                                                emp.getCatg(),
                                                loggedInRole))
                        .toList();

        log.info(
                "Authorized records for export : {}",
                authorizedEmployees.size());

        try (
                Workbook workbook =
                        new XSSFWorkbook();
                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()) {

            Sheet sheet =
                    workbook.createSheet(
                            "DNB Master Report");

            Row header =
                    sheet.createRow(0);

            String[] columns = {
                    "ID",
                    "NAME",
                    "DOB",
                    "DOJ",
                    "DOS",
                    "STATUS",
                    "STIPEND",
                    "DAILY RATE",
                    "SEX",
                    "BANK",
                    "ACCOUNT NO",
                    "PAN",
                    "CATEGORY",
                    "CATEGORY DESC",
                    "SPECIALITY",
                    "TRG DURATION"
            };

            for (int i = 0;
                    i < columns.length;
                    i++) {

                header.createCell(i)
                        .setCellValue(
                                columns[i]);
            }

            int rowNum = 1;

            for (DnbMast emp :
                    authorizedEmployees) {

                Row row =
                        sheet.createRow(
                                rowNum++);

                row.createCell(0)
                        .setCellValue(
                                emp.getId());

                row.createCell(1)
                        .setCellValue(
                                emp.getName());

                row.createCell(2)
                        .setCellValue(
                                String.valueOf(
                                        emp.getDob()));

                row.createCell(3)
                        .setCellValue(
                                String.valueOf(
                                        emp.getDoj()));

                row.createCell(4)
                        .setCellValue(
                                String.valueOf(
                                        emp.getDos()));

                row.createCell(5)
                        .setCellValue(
                                emp.getEmpStatus());

                row.createCell(6)
                        .setCellValue(
                                emp.getStipendRate());

                row.createCell(7)
                        .setCellValue(
                                emp.getDailyRate());

                row.createCell(8)
                        .setCellValue(
                                emp.getSexCode());

                row.createCell(9)
                        .setCellValue(
                                emp.getBankCd());

                row.createCell(10)
                        .setCellValue(
                                emp.getBankAcno());

                row.createCell(11)
                        .setCellValue(
                                emp.getPan());

                row.createCell(12)
                        .setCellValue(
                                emp.getCatg());

                row.createCell(13)
                        .setCellValue(
                                emp.getCatgDesc());

                row.createCell(14)
                        .setCellValue(
                                emp.getSpeciality());

                row.createCell(15)
                        .setCellValue(
                                emp.getTrgDuration());
            }

            workbook.write(out);

            log.info(
                    "Excel report generated successfully");

            return new ByteArrayInputStream(
                    out.toByteArray());

        } catch (Exception ex) {

            log.error(
                    "Error while exporting DNB Master Report",
                    ex);

            throw new RuntimeException(
                    "Unable to export report",
                    ex);
        }
    }
    
    @Override
    public List<Integer> getAvailableYymm() {

        log.info(
                "Fetching available YYMM values");

        return  
        		dnbMastRepository.findDistinctYymm();
    }
    
    
}