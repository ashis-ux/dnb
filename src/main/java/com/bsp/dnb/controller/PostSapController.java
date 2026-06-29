package com.bsp.dnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.PostSapStatusDto;
import com.bsp.dnb.service.PostSapService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/post-sap")
@Slf4j
public class PostSapController {

    @Autowired
    private PostSapService postSapService;

    @GetMapping("/status")
    public ResponseEntity<PostSapStatusDto> getStatus() {

        log.info(
                "Received request to fetch Post Into SAP status.");

        PostSapStatusDto response =
                postSapService.getStatus();

        log.info(
                "Post Into SAP status fetched successfully. "
                        + "Authorized: {}, "
                        + "Paybill Generated: {}, "
                        + "SAP Payment Available: {}",
                response.isAuthorized(),
                response.isPaybillGenerated(),
                response.isSapPaymentAvailable());

        return ResponseEntity.ok(
                response);
    }

    @PostMapping("/process")
    public ResponseEntity<String> processPayment() {

        log.info(
                "Received request to process payment into SAP.");

        String response =
                postSapService.processPayment();

        log.info(
                "Post Into SAP completed successfully.");

        return ResponseEntity.ok(
                response);
    }

} 