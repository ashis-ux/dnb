package com.bsp.dnb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.DnbMastDto;
import com.bsp.dnb.dto.UpdateMasterDto;
import com.bsp.dnb.service.DnbMastService;

import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/dnb")
@Slf4j
@CrossOrigin(origins = "*")
public class DnbMastController {
	
	private static final Logger log =
	        LoggerFactory.getLogger(DnbMastController.class);

	@Autowired
    private DnbMastService dnbMastService;

    @PostMapping
    public ResponseEntity<DnbMastDto> save(
            @RequestBody DnbMastDto dto) {

        log.info("Received request to create DNB record");

        DnbMastDto response = dnbMastService.save(dto);

        log.info("DNB record created successfully with ID : {}",
                response.getId());

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<DnbMastDto> update(
            @PathVariable Integer id,
            @RequestBody DnbMastDto dto) {

        log.info("Received request to update DNB record with ID : {}",
                id);

        dto.setId(id);

        DnbMastDto response =
                dnbMastService.update(dto);

        log.info("DNB record updated successfully with ID : {}",
                id);

        return ResponseEntity.ok(response);
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<DnbMastDto> findById(
            @PathVariable Integer id) {

        log.info("Received request to fetch DNB record with ID : {}",
                id);

        DnbMastDto response =
                dnbMastService.findById(id);

        return ResponseEntity.ok(response);
    }

     
    @GetMapping
    public ResponseEntity<Page<DnbMastDto>> findAll(
            @RequestParam(defaultValue = "0")
            int page,
            @RequestParam(defaultValue = "10")
            int size) {
        log.info("Received request to fetch DNB records. Page: {}, Size: {}",
                page, size);
        Page<DnbMastDto> response =
                dnbMastService.findAll(page, size);

        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/attendance-eligible")
    public ResponseEntity<List<DnbMastDto>>
    findEligibleForAttendance() {

        log.info(
                "Received request to fetch DNB Masters eligible for attendance");

        return ResponseEntity.ok(
                dnbMastService.findEligibleForAttendance());
    }
    
    @GetMapping("/search")

    public ResponseEntity<DnbMastDto> searchDnb(
            @RequestParam String value) {

        return ResponseEntity.ok(
        		dnbMastService.searchDnb(
                        value));
    }
    
    @PostMapping("/runMonthlyUpdate/{yymm}")
    public ResponseEntity<?> updateMaster(
            @PathVariable String yymm) {

        UpdateMasterDto dto =
                dnbMastService.runMonthlyUpdate(yymm);

        if (dto.getStatusCode() == 0) {

            return ResponseEntity
                    .badRequest()
                    .body(dto);
        }

        return ResponseEntity.ok(dto);
    }
    
 
}