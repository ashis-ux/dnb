package com.bsp.dnb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.bsp.dnb.dto.DnbPbillDto;
import com.bsp.dnb.service.DnbPbillService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dnb-pbill")
@CrossOrigin(origins = "*")
@Slf4j
@PreAuthorize("hasAuthority('APP_DNB')")
public class DnbPbillController {

    private static final Logger log =
            LoggerFactory.getLogger(
                    DnbPbillController.class);

    @Autowired
    private DnbPbillService dnbPbillService;

    @PostMapping
    public ResponseEntity<DnbPbillDto> save(
            @RequestBody DnbPbillDto dto) {

        log.info(
                "Received request to save PBill for YYMM : {}, ID : {}",
                dto.getYymm(),
                dto.getId());

        DnbPbillDto response =
                dnbPbillService.save(dto);

        log.info(
                "PBill saved successfully");

        return new ResponseEntity<>(
                response,
                HttpStatus.CREATED);
    }

    @PutMapping("/{yymm}/{id}")
    public ResponseEntity<DnbPbillDto> update(
            @PathVariable Integer yymm,
            @PathVariable Integer id,
            @RequestBody DnbPbillDto dto) {

        log.info(
                "Received request to update PBill for YYMM : {}, ID : {}",
                yymm,
                id);

        dto.setYymm(yymm);
        dto.setId(id);

        DnbPbillDto response =
                dnbPbillService.update(dto);

        log.info(
                "PBill updated successfully");

        return ResponseEntity.ok(
                response);
    }

    @GetMapping("/{yymm}/{id}")
    public ResponseEntity<DnbPbillDto> findById(
            @PathVariable Integer yymm,
            @PathVariable Integer id) {

        log.info(
                "Received request to fetch PBill for YYMM : {}, ID : {}",
                yymm,
                id);

        DnbPbillDto response =
                dnbPbillService.findById(
                        yymm,
                        id);

        return ResponseEntity.ok(
                response);
    }

    @GetMapping("/{yymm}")
    public ResponseEntity<List<DnbPbillDto>> findByYymm(
            @PathVariable Integer yymm) {

        log.info(
                "Received request to fetch PBills for YYMM : {}",
                yymm);

        List<DnbPbillDto> response =
                dnbPbillService.findByYymm(
                        yymm);

        return ResponseEntity.ok(
                response);
    }

}