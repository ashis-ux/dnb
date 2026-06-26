package com.bsp.dnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.ResetPayStatusDto;
import com.bsp.dnb.service.ResetPayService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reset-pay")
@Slf4j
public class ResetPayDataController {

    @Autowired
    private ResetPayService resetPayService;

    @GetMapping("/status")
    public ResponseEntity<ResetPayStatusDto> getStatus() {

        log.info("Received request to fetch Reset Pay Data status.");

        ResetPayStatusDto response =
                resetPayService.getStatus();

        log.info(
                "Reset Pay Data status fetched successfully. "
                + "Authorized: {}, Paybill Exists: {}",
                response.isAuthorized(),
                response.isPaybillExists());

        return ResponseEntity.ok(
                response);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> resetPayData() {

        log.info(
                "Received request to reset pay data.");

        String message =
                resetPayService.resetPayData();

        log.info(
                "Reset Pay Data completed successfully.");

        return ResponseEntity.ok(
                message);
    }
}