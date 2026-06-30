package com.bsp.dnb.controller;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.AdjustmentReportDto;
import com.bsp.dnb.service.AdjustmentReportService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/adjustment-report")
@CrossOrigin(origins = "*")
@Slf4j
@PreAuthorize("hasAuthority('APP_DNB')")
public class AdjustmentReportController {

 

    @Autowired
    private AdjustmentReportService adjustmentReportService;

    @GetMapping("/{yymm}")
    public ResponseEntity<Page<AdjustmentReportDto>> getAdjustmentReport(
            @PathVariable Integer yymm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Received request to fetch adjustment report for YYMM={}, page={}, size={}",
                yymm, page, size);

        Page<AdjustmentReportDto> report =
                adjustmentReportService.getAdjustmentReport(yymm, page, size);

        log.info("Successfully fetched adjustment report for YYMM={}", yymm);

        return ResponseEntity.ok(report);
    }

    @GetMapping("/yymm")
    public ResponseEntity<List<Integer>> getYymm() {

        log.info("Received request to fetch available YYMM values.");

        List<Integer> yymmList =
                adjustmentReportService.getAdjustmentYymm();

        log.info("Successfully fetched {} YYMM value(s).", yymmList.size());

        return ResponseEntity.ok(yymmList);
    }

    @GetMapping("/export/{yymm}")
    public ResponseEntity<InputStreamResource> exportAdjustmentReport(
            @PathVariable Integer yymm) {

        log.info("Received request to export adjustment report for YYMM={}", yymm);

        ByteArrayInputStream excel =
                adjustmentReportService.exportAdjustmentReport(yymm);

        HttpHeaders headers = new HttpHeaders();
        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Adjustment_Report_" + yymm + ".xlsx");

        log.info("Adjustment report exported successfully for YYMM={}", yymm);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(excel));
    }
}