package com.bsp.dnb.controller;

import com.bsp.dnb.dto.PayslipSearchDto;
import com.bsp.dnb.service.PayslipService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payslip")
@PreAuthorize("hasAuthority('APP_DNB')")
public class PayslipController {

	
	@Autowired
    private PayslipService payslipService;

    @GetMapping("/search")
    public ResponseEntity<Page<PayslipSearchDto>> searchPayslips(

            @RequestParam(required = false) Integer id,
            @RequestParam(required = false) String pan,
            @RequestParam(required = false) Integer yymm,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        log.info("Payslip search request received | id={} | pan={} | yymm={} | page={} | size={}",
                id, pan, yymm, page, size);

        Page<PayslipSearchDto> result =
                payslipService.searchPayslips(id, pan, yymm, page, size);

        log.info("Payslip search response | totalElements={} | totalPages={}",
                result.getTotalElements(),
                result.getTotalPages());

        return ResponseEntity.ok(result);
    }

   

    @GetMapping("/view/{yymm}/{id}")
    public ResponseEntity<byte[]> viewPayslip(

            @PathVariable Integer yymm,
            @PathVariable Integer id) {

        log.info("View payslip request | yymm={} | id={}", yymm, id);

        byte[] pdf = payslipService.generatePayslip(yymm, id);

        log.info("Payslip PDF generated for view | size={} bytes", pdf.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDisposition(
                ContentDisposition.inline()
                        .filename("Payslip_" + id + "_" + yymm + ".pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }



    @GetMapping("/download/{yymm}/{id}")
    public ResponseEntity<byte[]> downloadPayslip(

            @PathVariable Integer yymm,
            @PathVariable Integer id) {

        log.info("Download payslip request | yymm={} | id={}", yymm, id);

        byte[] pdf = payslipService.generatePayslip(yymm, id);

        log.info("Payslip PDF generated for download | size={} bytes", pdf.length);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);

        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("Payslip_" + id + "_" + yymm + ".pdf")
                        .build()
        );

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}