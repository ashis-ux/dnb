package com.bsp.dnb.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bsp.dnb.dto.AdjustmentEntryDto;
import com.bsp.dnb.dto.DnbAdjDto;
import com.bsp.dnb.service.DnbAdjService;
 
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/dnb-adj")
@CrossOrigin(origins = "*")
@Slf4j
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
	
//	@GetMapping("/{yymm}/{id}/{forym}")
//	public ResponseEntity<DnbAdjDto> findById(
//	        @PathVariable Integer yymm,
//	        @PathVariable Integer id,
//	        @PathVariable Integer forym) {
//
//	    return ResponseEntity.ok(
//
//	            dnbAdjService.findById(
//	                    yymm,
//	                    id,
//	                    forym));
//	}
	
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
	
}
