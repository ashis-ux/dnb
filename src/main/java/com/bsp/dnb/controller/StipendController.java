package com.bsp.dnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.StipendStatusDto;
import com.bsp.dnb.service.StipendService;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("/api/stipend")
@Slf4j
@PreAuthorize("hasAuthority('APP_DNB')")
public class StipendController {

	@Autowired
    private StipendService stipendService;

    @PostMapping("/create")
    public ResponseEntity<String> createStipend() {

        log.info("Received request to create stipend.");

        String message =
                stipendService.createStipend();

        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/access")
    public ResponseEntity<StipendStatusDto> getStatus() {

        log.info("Received request to fetch stipend access status.");

        StipendStatusDto response =
                stipendService.getStatus();

        log.info(
                "Stipend access status fetched successfully. "
                + "Authorized: {}, Paybill Generated: {}",
                response.isAuthorized(),
                response.isPaybillGenerated());

        return ResponseEntity.ok(response);
    }
}
