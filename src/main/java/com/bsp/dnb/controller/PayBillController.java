package com.bsp.dnb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.PayBillDto;
import com.bsp.dnb.service.PayBillService;

@RestController
@RequestMapping("/api/paybill")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class PayBillController {
	
	@Autowired
    private PayBillService demoService;

    @PostMapping("/process/{input}")
    public ResponseEntity<PayBillDto> process(
            @PathVariable Integer input) {

    	PayBillDto response =
                demoService.process(input);

        return ResponseEntity.ok(response);
    }

}
