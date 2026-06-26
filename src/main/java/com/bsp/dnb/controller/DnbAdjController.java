package com.bsp.dnb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.AdjustmentEntryDto;
import com.bsp.dnb.dto.DnbAdjDto;
import com.bsp.dnb.service.DnbAdjService;
 
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dnb-adj")
@CrossOrigin(origins = "*")
@Slf4j
@PreAuthorize("hasAuthority('APP_DNB')")
public class DnbAdjController {
	
	private static final Logger log =
	        LoggerFactory.getLogger(DnbAdjController.class);
	
	 @Autowired
	    private DnbAdjService dnbAdjService;
	
	@PostMapping("/bulk")
	public ResponseEntity<List<DnbAdjDto>>
	saveAll(
	        @RequestBody
	        List<DnbAdjDto> dtoList) {

	    log.info(
	            "Received request to save {} adjustment records",
	            dtoList.size());

	    return new ResponseEntity<>(

	            dnbAdjService.saveAll(
	                    dtoList),

	            HttpStatus.CREATED);
	}
 
	
	@GetMapping("/entry/{yymm}")
	public ResponseEntity<List<AdjustmentEntryDto>>
	getAdjustmentEntry(
	        @PathVariable Integer yymm) {

	    log.info(
	            "Received request to fetch adjustment entry for YYMM : {}",
	            yymm);

	    return ResponseEntity.ok(
	            dnbAdjService.getAdjustmentEntry(
	                    yymm));
	}
	
	@GetMapping("/calculate")
	public ResponseEntity<Integer> calculateAmount(
	        @RequestParam Integer id,
	        @RequestParam Integer forym,
	        @RequestParam Integer days) {

	    log.info(
	            "Calculate adjustment amount request received. id={}, forym={}, days={}",
	            id,
	            forym,
	            days);

	    Integer amount =
	    		dnbAdjService.calculateAmount(
	                    id,
	                    forym,
	                    days);

	    log.info(
	            "Calculated adjustment amount={} for id={}, forym={}",
	            amount,
	            id,
	            forym);

	    return ResponseEntity.ok(amount);
	}
	
}
