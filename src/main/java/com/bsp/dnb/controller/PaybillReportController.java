package com.bsp.dnb.controller;

import java.io.ByteArrayInputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.PaybillReportDto;
import com.bsp.dnb.service.PaybillReportService;

@RestController
@RequestMapping("/api/paybill-report")
@CrossOrigin(origins = "*")
public class PaybillReportController {

    @Autowired
    private PaybillReportService
            paybillReportService;

    @GetMapping("/{yymm}")
    public ResponseEntity<
            Page<PaybillReportDto>>
    getPaybillReport(

            @PathVariable
            Integer yymm,

            @RequestParam(
                    defaultValue = "0")
            int page,

            @RequestParam(
                    defaultValue = "10")
            int size) {

        return ResponseEntity.ok(

                paybillReportService
                        .getPaybillReport(
                                yymm,
                                page,
                                size));
    }

    @GetMapping("/export/{yymm}")
    public ResponseEntity<
            InputStreamResource>
    exportPaybillReport(

            @PathVariable
            Integer yymm) {

        ByteArrayInputStream excelFile =

                paybillReportService
                        .exportPaybillReport(
                                yymm);

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Paybill_Report_"
                        + yymm
                        + ".xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(
                        MediaType.parseMediaType(
                                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(
                        new InputStreamResource(
                                excelFile));
    }
}