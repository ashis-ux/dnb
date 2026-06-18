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
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.AttendanceReportDto;
import com.bsp.dnb.service.AttendanceReportService;

@RestController
@RequestMapping("/api/attendance-report")
@CrossOrigin(origins = "*")
public class AttendanceReportController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    AttendanceReportController.class);

    @Autowired
    private AttendanceReportService
            attendanceReportService;

    @GetMapping("/{yymm}")
    public ResponseEntity<
            Page<AttendanceReportDto>>
    getAttendanceReport(

            @PathVariable
            Integer yymm,

            @RequestParam(
                    defaultValue = "0")
            int page,

            @RequestParam(
                    defaultValue = "10")
            int size) {

        log.info(
                "Fetching attendance report for YYMM : {}, Page : {}, Size : {}",
                yymm,
                page,
                size);

        return ResponseEntity.ok(

                attendanceReportService
                        .getAttendanceReport(
                                yymm,
                                page,
                                size));
    }

    @GetMapping("/export/{yymm}")
    public ResponseEntity<
            InputStreamResource>
    exportAttendanceReport(

            @PathVariable
            Integer yymm) {

        log.info(
                "Exporting attendance report for YYMM : {}",
                yymm);

        ByteArrayInputStream excelFile =

                attendanceReportService
                        .exportAttendanceReport(
                                yymm);

        HttpHeaders headers =
                new HttpHeaders();

        headers.add(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=Attendance_Report_"
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

    @GetMapping("/yymm")
    public ResponseEntity<
            List<Integer>>
    getAvailableYymm() {

        log.info(
                "Fetching attendance YYMM values");

        return ResponseEntity.ok(

                attendanceReportService
                        .getAttendanceYymm());
    }
}