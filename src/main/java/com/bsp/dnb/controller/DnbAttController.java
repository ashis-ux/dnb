package com.bsp.dnb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.AttendanceEntryDto;
import com.bsp.dnb.dto.DnbAttDto;
import com.bsp.dnb.service.DnbAttService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dnb-att")
@CrossOrigin(origins = "*")
@Slf4j
public class DnbAttController {
	
	private static final Logger log =
	        LoggerFactory.getLogger(DnbMastController.class);

    @Autowired
    private DnbAttService dnbAttService;
 
    @PostMapping
    public ResponseEntity<DnbAttDto> save(
            @RequestBody DnbAttDto dto) {

        log.info("Received request to save attendance for ID : {}, YYMM : {}",
                dto.getId(), dto.getYymm());

        DnbAttDto response = dnbAttService.save(dto);

        log.info("Attendance saved successfully");

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }
    
 
    @GetMapping("/{yymm}")
    public ResponseEntity<List<DnbAttDto>> findByYymm(
            @PathVariable Integer yymm) {

        log.info(
                "Received request to fetch attendance for YYMM : {}",
                yymm);

        List<DnbAttDto> response =
                dnbAttService.findByYymm(yymm);

        return ResponseEntity.ok(response);
    }


    @PutMapping("/{yymm}/{id}")
    public ResponseEntity<DnbAttDto> update(
            @PathVariable Integer yymm,
            @PathVariable Integer id,
            @RequestBody DnbAttDto dto) {

        log.info("Received request to update attendance for ID : {}, YYMM : {}",
                id, yymm);

        dto.setYymm(yymm);
        dto.setId(id);

        DnbAttDto response =
                dnbAttService.update(dto);

        log.info("Attendance updated successfully");

        return ResponseEntity.ok(response);
    }


    @GetMapping("/{yymm}/{id}")
    public ResponseEntity<DnbAttDto> findById(
            @PathVariable Integer yymm,
            @PathVariable Integer id) {

        log.info("Received request to fetch attendance for ID : {}, YYMM : {}",
                id, yymm);

        DnbAttDto response =
                dnbAttService.findById(yymm, id);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/bulk")
    public ResponseEntity<List<DnbAttDto>> saveAll(
            @RequestBody List<DnbAttDto> dtoList) {

        log.info("Received request to save {} attendance records",
                dtoList.size());

        List<DnbAttDto> response =
                dnbAttService.saveAll(dtoList);

        log.info("Successfully saved {} attendance records",
                response.size());

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }
    
    @GetMapping("/entry/{yymm}")
    public ResponseEntity<List<AttendanceEntryDto>>
    getAttendanceEntry(
            @PathVariable Integer yymm) {

        log.info(
                "Received request to fetch attendance entry for YYMM : {}",
                yymm);

        List<AttendanceEntryDto> response =
                dnbAttService.getAttendanceEntry(yymm);

        log.info(
                "Fetched {} attendance rows for YYMM : {}",
                response.size(),
                yymm);

        return ResponseEntity.ok(response);
    }

   
 
}