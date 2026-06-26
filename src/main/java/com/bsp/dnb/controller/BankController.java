package com.bsp.dnb.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.service.BankCdsService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/bank")
@Slf4j
@PreAuthorize("hasAuthority('APP_DNB')")
public class BankController {

    @Autowired
    private BankCdsService service;

    @GetMapping("/bank-code")
    public ResponseEntity<Map<String, Integer>>
            getBankCode(

            @RequestParam
            String bankName,

            @RequestParam
            String ifscCode) {

        log.info(
                "Received request to fetch Bank Code. Bank Name : {}, IFSC : {}",
                bankName,
                ifscCode);

        Integer bankCode =
                service.getBankCode(
                        bankName.trim(),
                        ifscCode.trim());

        Map<String, Integer> response =
                new HashMap<>();

        response.put(
                "bankCode",
                bankCode);

        log.info(
                "Returning Bank Code : {}",
                bankCode);

        return ResponseEntity.ok(
                response);
    }
    
    @GetMapping("/banks")
    public ResponseEntity<List<String>> getBanks() {

        log.info("Request received to fetch bank names.");

        List<String> banks =
                service.getAllBankNames();

        log.info(
                "Returning {} bank names.",
                banks.size());

        return ResponseEntity.ok(banks);
    }
    
    @GetMapping("/bank-names")
    public ResponseEntity<List<String>> getBankNames(
            @RequestParam String ifscCode) {

        log.info(
                "Request received to fetch bank names for IFSC : {}",
                ifscCode);

        return ResponseEntity.ok(
                service.getBankNamesByIfsc(
                        ifscCode));
    }

}