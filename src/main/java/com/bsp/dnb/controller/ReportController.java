package com.bsp.dnb.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.DnbMasterReportDto;
import com.bsp.dnb.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/dnb-master/{yymm}")
    public ResponseEntity<Page<DnbMasterReportDto>>
    getReport(
            @PathVariable Integer yymm,

            @RequestParam(
                    defaultValue = "0")
            int page,

            @RequestParam(
                    defaultValue = "10")
            int size) {

        return ResponseEntity.ok(
                reportService
                        .getDnbMasterReport(
                                yymm,
                                page,
                                size));
    }

    @GetMapping(
            "/dnb-master/export/{yymm}")
    public ResponseEntity<Resource>
    exportExcel(
            @PathVariable
            Integer yymm) {

        ByteArrayInputStream stream =
                reportService
                        .exportDnbMasterReport(
                                yymm);

        InputStreamResource file =
                new InputStreamResource(
                        stream);

        return ResponseEntity.ok()

                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=DNB_MASTER_REPORT.xlsx")

                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))

                .body(file);
    }
    
    @GetMapping("/yymm")
    public ResponseEntity<List<Integer>>
    getAvailableYymm() {

        return ResponseEntity.ok(
                reportService
                        .getAvailableYymm());
    }
}

