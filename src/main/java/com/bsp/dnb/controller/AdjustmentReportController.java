package com.bsp.dnb.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.AdjustmentReportDto;
import com.bsp.dnb.service.AdjustmentReportService;

@RestController
@RequestMapping("/api/adjustment-report")
@CrossOrigin(origins = "*")
public class AdjustmentReportController {

    @Autowired
    private AdjustmentReportService adjustmentReportService;

    @GetMapping("/{yymm}")
    public ResponseEntity<Page<AdjustmentReportDto>>
    getAdjustmentReport(
            @PathVariable Integer yymm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return ResponseEntity.ok(
                adjustmentReportService.getAdjustmentReport(
                        yymm,
                        page,
                        size));
    }

    @GetMapping("/yymm")
    public ResponseEntity<List<Integer>>
    getYymm() {

        return ResponseEntity.ok(
                adjustmentReportService.getAdjustmentYymm());
    }

    @GetMapping("/export/{yymm}")
    public ResponseEntity<InputStreamResource>
    exportAdjustmentReport(
            @PathVariable Integer yymm) {

        ByteArrayInputStream excel =
                adjustmentReportService
                        .exportAdjustmentReport(
                                yymm);

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Adjustment_Report_"
                        + yymm
                        + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(
                        new InputStreamResource(
                                excel));
    }
}